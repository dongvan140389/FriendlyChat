package com.example.dongvan.friendlychat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.dongvan.adapter.MessageAdapter;
import com.example.dongvan.model.FriendlyMessage;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
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

import static android.R.attr.id;
import static com.example.dongvan.friendlychat.ListFriendActivity.mUsername;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    public static final String ANONYMOUS = "anonymous";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private MessageAdapter mMessageAdapter;
    private ProgressBar mProgressBar;
    private ImageButton mPhotoPickerButton;
    private EditText mMessageEditText;
    private Button mSendButton;

    //Biến trả về hình đc chọn
    private static final int RC_PHOTO_PICKER = 2;

    // Firebase instance variables, tạo các biến thực thể trên firebase
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private ChildEventListener mChildEventListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosStorageReference;
    String idUser="";
    String idMyUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Lấy dữ liệu từ intent gửi qua
        Intent i = getIntent();
        idUser = i.getStringExtra("ID_USER");
        idMyUser = i.getStringExtra("ID_MYUSER");

        // Initialize Firebase components, tạo các thành phần Firebase tham chiếu đến nút con messages
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();



        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mChatPhotosStorageReference = mFirebaseStorage.getReference().child("chat_photos");

        // Ánh xạ đến các view
        addControl();

        // Initialize message ListView and its adapter, Khởi tạo tin nhắn và adapter cho listview
        List<FriendlyMessage> friendlyMessages = new ArrayList<>();
        mMessageAdapter = new MessageAdapter(this, R.layout.item_message, friendlyMessages);
        mMessageListView.setAdapter(mMessageAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        // ImagePickerButton shows an image picker to upload a image for a message
        //Click button mPhotoPickerButton thì upload hình đã chọn cho tin nhắn
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Fire an intent to show an image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send, Hiện send button khi có nhập tin nhắn để gửi
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText, khi đã send thì set mMessageEditText lại thành rỗng
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Send messages on click
                FriendlyMessage friendlyMessage = new FriendlyMessage(mMessageEditText.getText().toString(), mUsername, null,idMyUser,idUser);
                mMessagesDatabaseReference.push().setValue(friendlyMessage);
                // Clear input box
                mMessageEditText.setText("");
                attachDatabaseReadListener();
            }
        });
    }

    private void addControl() {
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mMessageListView = (ListView) findViewById(R.id.messageListView);
        mPhotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mSendButton = (Button) findViewById(R.id.sendButton);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMessageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        //Xử lý sự kiện có thay đổi tin nhắn
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    mMessageAdapter.add(friendlyMessage);
                }

                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            // Get a reference to store file at chat_photos/<FILENAME>
            StorageReference photoRef = mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            // Upload file to Firebase Storage
            photoRef.putFile(selectedImageUri)
                    .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // When the image has successfully uploaded, we get its download URL
                            Uri downloadUrl = taskSnapshot.getDownloadUrl();

                            // Set the download URL to the message box, so that the user can send it to the database
                            FriendlyMessage friendlyMessage = new FriendlyMessage(null, mUsername, downloadUrl.toString(),idMyUser,idUser);
                            mMessagesDatabaseReference.push().setValue(friendlyMessage);
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setIsSmartLockEnabled(false)
                                .setProviders(
                                        AuthUI.EMAIL_PROVIDER,
                                        AuthUI.GOOGLE_PROVIDER)
                                .build(),
                        ListFriendActivity.RC_SIGN_IN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
