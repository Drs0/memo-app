package com.example.notesspace;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
    private FloatingActionButton mcreatenote;
    private FirebaseAuth firebaseAuth;
    private RecyclerView mrecyclerView;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    private FirestoreRecyclerAdapter<firebaseModel,NoteViewHolder> noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        mcreatenote = findViewById(R.id.createnote);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        mrecyclerView = findViewById(R.id.recyclerview);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Notes");


        mcreatenote.setOnClickListener(v -> startActivity(new Intent(NotesActivity.this,CreateNoteActivity.class)));

        Query query = firestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").orderBy("title",Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<firebaseModel> allusernote = new FirestoreRecyclerOptions.Builder<firebaseModel>().setQuery(query,firebaseModel.class).build();
        noteAdapter = new FirestoreRecyclerAdapter<firebaseModel, NoteViewHolder>(allusernote) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull firebaseModel model) {
                ImageView popup = holder.itemView.findViewById(R.id.menupopupbutton);
                //Random Colors
                int colorCode = getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colorCode));

                //Fetch data from firebase to recycler view
                holder.notetitle.setText(model.getTitle());
                holder.notecontent.setText(model.getContent());
                String noteId = noteAdapter.getSnapshots().getSnapshot(position).getId();

                // add click listener
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(),NoteDetails.class);
                        intent.putExtra("title",model.getTitle());
                        intent.putExtra("content",model.getContent());
                        intent.putExtra("noteId",noteId);
                        v.getContext().startActivity(intent);
                    }
                });
                popup.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Edit").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                Intent intent = new Intent(v.getContext(),editNote.class);
                                intent.putExtra("title",model.getTitle());
                                intent.putExtra("content",model.getContent());
                                intent.putExtra("noteId",noteId);
                                v.getContext().startActivity(intent);
                                return false;
                            }
                        });
                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(@NonNull MenuItem item) {
                                DocumentReference documentReference =
                                        firestore.collection("notes").document(firebaseUser.getUid()).collection("myNotes").document(noteId);
                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(),"Note Deleted",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),"Error In Deleting Note!",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };
        mrecyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        mrecyclerView.setLayoutManager(staggeredGridLayoutManager);
        mrecyclerView.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView notetitle,notecontent;
        private LinearLayout mnote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle = itemView.findViewById(R.id.notetitle);
            notecontent = itemView.findViewById(R.id.notecotent);
            mnote = itemView.findViewById(R.id.note);
        }

        public LinearLayout getMnote() {
            return mnote;
        }

        public void setMnote(LinearLayout mnote) {
            this.mnote = mnote;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(NotesActivity.this,MainActivity.class));

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter != null){
            noteAdapter.stopListening();
        }
    }
    private int getRandomColor(){
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.color3);
        colorList.add(R.color.color1);
        colorList.add(R.color.gray);
        colorList.add(R.color.green);
        colorList.add(R.color.lightgreen);
        colorList.add(R.color.color5);
        colorList.add(R.color.color4);
        colorList.add(R.color.color2);
        colorList.add(R.color.skyblue);
        colorList.add(R.color.teal_700);
        colorList.add(R.color.pink);
        colorList.add(R.color.purple_500);
        Random random = new Random();
        int number = random.nextInt(colorList.size());
        return colorList.get(number);
    }
}