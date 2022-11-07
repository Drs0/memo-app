package com.example.notesspace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class editNote extends AppCompatActivity {
    Intent data;
    EditText medittitleofnote,meditecontentofnote;
    FloatingActionButton editsave;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        medittitleofnote = findViewById(R.id.edittitleofnote);
        meditecontentofnote = findViewById(R.id.editcontentofnote);
        editsave = findViewById(R.id.saveeditnote);
        data = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        Toolbar toolbar = findViewById(R.id.toolbarofeditnote);
        setSupportActionBar(toolbar);
        String noteTitle  = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");
        medittitleofnote.setText(noteTitle);
        meditecontentofnote.setText(noteContent);
        editsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  String newTitle = medittitleofnote.getText().toString();
                  String newContent = meditecontentofnote.getText().toString();
                  if (newTitle.isEmpty()||newContent.isEmpty()){
                      Toast.makeText(getApplicationContext(),"Both Fields Are Required!",Toast.LENGTH_SHORT).show();
                  }else{
                      DocumentReference documentReference =
                              firestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(data.getStringExtra("noteId"));
                      Map<String,Object> note = new HashMap<>();
                      note.put("title",newTitle);
                      note.put("content",newContent);
                      documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                          @Override
                          public void onSuccess(Void unused) {
                              Toast.makeText(getApplicationContext(),"Note Edited",Toast.LENGTH_SHORT).show();
                              Intent intent = new Intent(editNote.this,NotesActivity.class);
                              startActivity(intent);
                          }
                      }).addOnFailureListener(new OnFailureListener() {
                          @Override
                          public void onFailure(@NonNull Exception e) {
                              Toast.makeText(getApplicationContext(),"Oops! Something Went Wrong",Toast.LENGTH_SHORT).show();
                          }
                      });
                  }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}