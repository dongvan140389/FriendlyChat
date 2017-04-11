package com.example.dongvan.friendlychat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dongvan.adapter.UserAdapter;
import com.example.dongvan.model.UserChat;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.dongvan.friendlychat.MainActivity.ANONYMOUS;

public class ListFriendActivity extends AppCompatActivity {

    private ListView listUserChat;
    private UserAdapter adapterUserChat;

    //Firebase instance variables
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDataFriend;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //Biến kiểm tra đăng nhập user
    public static final int RC_SIGN_IN = 1;

    public static String mUsername;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend);
        addControl();
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDataFriend = mFirebaseDatabase.getReference().child("friends");
        addEvents();

        mDataFriend.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserChat userChat = dataSnapshot.getValue(UserChat.class);
                adapterUserChat.add(userChat);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // user is signed in
                    onSignedInInitialize(user.getDisplayName());
                    Log.d("bbbb ", "chạy tới đây 2222");
                } else {
                    // user is signed out
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);

                }
            }
        };
    }

    private void onSignedInInitialize(String username) {
        mUsername = username;
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        //mMessageAdapter.clear();
    }



    private void addEvents() {
        listUserChat.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserChat friend = adapterUserChat.getItem(position);
                Intent i = new Intent(ListFriendActivity.this,MainActivity.class);
                //i.putExtra("QUANLY",user);
                i.putExtra("ID_USER",friend.getUID());
                i.putExtra("ID_MYUSER",user.getUid());
                startActivity(i);
            }
        });
    }

    private void addControl() {
        listUserChat = (ListView) findViewById(R.id.lvFriend);
        adapterUserChat = new UserAdapter(ListFriendActivity.this,R.layout.item_userchat);
        listUserChat.setAdapter(adapterUserChat);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
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
