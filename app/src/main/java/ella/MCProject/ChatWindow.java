package ella.MCProject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.facebook.login.LoginManager;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class ChatWindow extends AppCompatActivity {
    private static final String TAG = "ChatWindow";

    static final int RC_PHOTO_PICKER = 1;

    private Button sendBtn;
    private FloatingActionButton publishBtn;
    private EditText messageTxt;
    private RecyclerView messagesList;
    private ChatMessageAdapter adapter;
    private ImageButton imageBtn;

    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private String chatUniqueId;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_window);

        sendBtn = (Button) findViewById(R.id.sendBtn);
        publishBtn = (FloatingActionButton) findViewById(R.id.publish);
        messageTxt = (EditText) findViewById(R.id.messageTxt);
        messagesList = (RecyclerView) findViewById(R.id.messagesList);
        imageBtn = (ImageButton) findViewById(R.id.imageBtn);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        messagesList.setHasFixedSize(false);
        messagesList.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        chatUniqueId = intent.getExtras().getString("chatUniqueId");
        Log.v("UNIQUEID", chatUniqueId);

        // Show an image picker when the user wants to upload an imasge
        imageBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        adapter = new ChatMessageAdapter(this);
        messagesList.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                messagesList.smoothScrollToPosition(adapter.getItemCount());
            }
        });

        // Get the Firebase app and all primitives we'll use
        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);

        databaseRef = database.getReference("chat");
        usersRef = databaseRef.child(chatUniqueId);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("CHATWINDOW", "wrote " + messageTxt.getText().toString());
                ChatMessage chatMessage = new ChatMessage(LoginActivity.getMainUser(), messageTxt.getText().toString());
                // Push the chat message to the database
                usersRef.push().setValue(chatMessage);
                messageTxt.setText("");
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.v("PUBLISH", adapter.toString());

                final LoginManager publishManager = LoginManager.getInstance();
                List<String> permissionNeeds = new ArrayList<String>();
                permissionNeeds.add("publish_actions");
                publishManager.logInWithPublishPermissions(ChatWindow.this, permissionNeeds);
                ShareDialog shareDialog = new ShareDialog(ChatWindow.this);
                ShareLinkContent content = new ShareLinkContent.Builder()
                        .setContentUrl(Uri.parse("http://www.facebook.com"))
                        .setQuote(adapter.toString())
                        .build();
                shareDialog.show(content);
            }
        });

        // Listen for when child nodes get added to the collection
        usersRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot snapshot, String s) {
                // Get the chat message from the snapshot and add it to the UI
                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                adapter.addMessage(chatMessage);
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }
            public void onChildRemoved(DataSnapshot dataSnapshot) { }
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to the location where we'll store our photos
            storageRef = storage.getReference("chat_photos");
            // Get a reference to store file at chat_photos/<FILENAME>
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            StorageReference storageRef = taskSnapshot.getMetadata().getReference();
                            Task<Uri> downloadUrl = storageRef.getDownloadUrl();
                            // Set the download URL to the message box, so that the user can send it to the database
                            messageTxt.setText(downloadUrl.toString());
                        }
                    });
        }
    }
}
