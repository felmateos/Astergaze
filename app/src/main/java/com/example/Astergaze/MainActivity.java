package com.example.Astergaze;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    TextView mFullName, mEmail, verifyMsg, messagesBtn;
    Button resendCode;
    ImageButton mAddPicture;
    ImageView resendMail, profileImageView;
    FloatingActionButton confirmPic;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID, otherUsersID;

    Uri imageUriAux;

    StorageReference storageReference;
    String TAG="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int statusBarColor = android.graphics.Color.parseColor("#20111111");
        int navBarColor = android.graphics.Color.parseColor("#20111111");
        MainActivity.setWindowStatusNav(getWindow(), statusBarColor, navBarColor);

        setContentView(R.layout.activity_main);

        mFullName = findViewById(R.id.name);
        mEmail = findViewById(R.id.email);

        resendMail = findViewById(R.id.resend_mail);
        messagesBtn = findViewById(R.id.messages_btn);
        mAddPicture = findViewById(R.id.add_profile_pic);
        profileImageView = findViewById(R.id.profile_pic);
        confirmPic = findViewById((R.id.confirm_pic));

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        otherUsersID = fAuth.getUid();

        storageReference = FirebaseStorage.getInstance().getReference();

        final StorageReference profileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");

        //final String fullName = fAuth.getCurrentUser().getDisplayName();
        //final String email = fAuth.getCurrentUser().getEmail();

        // Função que permite ao usuário escolher uma imagem para seu perfil
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                if(profileRef != null) {
                    Picasso.get().load(uri).into(profileImageView);
                    String profileUrl = uri.toString();

                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    /*Map<String,Object> user = new HashMap<>();
                    user.put("fName", );
                    user.put("email", email);
                    user.put("uuid", userID);
                    user.put("profileUrl", profileUrl);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("xiliu", "OnSuccess: user profile is created for " + userID);
                        }
                    });*/
                    Map<String, Object> data = new HashMap<>();
                    data.put("profileUrl", profileUrl);

                    fStore.collection("users").document(userID).set(data, SetOptions.merge());
                    // Adiciona a url da imagem cadastrada no banco ao documento do usuário
                }
            }
        });
        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot != null){
                    mFullName.setText(documentSnapshot.getString("fName"));
                    mEmail.setText(documentSnapshot.getString("email"));

                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
        confirmPic.setEnabled(false);
        confirmPic.setClickable(false);
        confirmPic.setAlpha(0f);

        final FirebaseUser fUser = fAuth.getCurrentUser();

        mAddPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPicure();
                confirmPic.setEnabled(true);
                confirmPic.setClickable(true);
                confirmPic.setAlpha(1f);
                mAddPicture.setAlpha(0f);
            }
        });
        confirmPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageToFirebase(imageUriAux);
                confirmPic.setEnabled(false);
                confirmPic.setClickable(false);
                confirmPic.setAlpha(0f);
            }
        });

        // Verifica se o e-mail do usuário está verificado
        if(!fUser.isEmailVerified()){
            resendMail.setVisibility(View.VISIBLE);

            resendMail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder emailVerify = new AlertDialog.Builder(v.getContext());
                    emailVerify.setTitle("E-mail não verificado!");
                    emailVerify.setMessage("Gostaria de receber um link em seu e-mail para verificarmos sua senha?");

                    emailVerify.setPositiveButton("sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(v.getContext(), "E-mail de verificação enviado", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "OnFailure: E-mail no sent " + e.getMessage());
                                }
                            });
                        }
                    });

                    emailVerify.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // close the dialog
                        }
                    });

                    emailVerify.create().show();

                }
            });
        }
        messagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                startActivity(new Intent(getApplicationContext(), MessagesActivity.class));
            }
        });
    }

    // Função de Logout
    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();//logout
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    // Função para selecionar uma imagem na galeria
    private void selectPicure() {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent,1000);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1000){
            if(resultCode == Activity.RESULT_OK){
                Uri imageUri = data.getData();
                imageUriAux = imageUri;

                profileImageView.setImageURI(imageUri);

            }
        }

    }

    // Armazena a imagem selecionada no Firebase
    private void uploadImageToFirebase(Uri imageUri) {
        // uplaod image to firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid()+"/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImageView);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Failed.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Modifica a barra de notificações
    public static void setWindowStatusNav(android.view.Window window, int statusbarColor, int navbarColor) {

        int flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT_WATCH) {
            window.getAttributes().flags |= flags;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
            window.getDecorView().setSystemUiVisibility(uiVisibility);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getAttributes().flags &= ~flags;

            window.setStatusBarColor(statusbarColor);
            window.setNavigationBarColor(navbarColor);
        }
    }
}
