package com.example.assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "activity_quiz";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT =0;
    private Button mTrueButton;
    private Button mFalseButton;
    private Button mNextButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;

    private Question[] mQuestionBank = new Question[]{
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_usha, true),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private void updateQuestion(){
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);

        if(mQuestionBank[mCurrentIndex] == mQuestionBank[1]){
            mTrueButton.setText("Beautiful");
            mFalseButton.setText("Ugly");
        } else {
            mTrueButton.setText("True");
            mFalseButton.setText("False");
        }
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();

        int messageResId = 0;


        if(mIsCheater){
            if (mQuestionBank[mCurrentIndex] == mQuestionBank[1]) {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.judgement_toast;
                } else {
                    messageResId = R.string.usha_false_toast;
                }
            } else {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.judgement_toast;
                } else {
                    messageResId = R.string.incorrect_toast;
                }
            }
        }else {

            if (mQuestionBank[mCurrentIndex] == mQuestionBank[1]) {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.usha_true_toast;
                } else {
                    messageResId = R.string.usha_false_toast;
                }
            } else {
                if (userPressedTrue == answerIsTrue) {
                    messageResId = R.string.correct_toast;
                } else {
                    messageResId = R.string.incorrect_toast;
                }
            }
        }
            Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        //int question = mQuestionBank[mCurrentIndex].getTextResId();
        //mQuestionTextView.setText(question);

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,R.string.incorrectToast,Toast.LENGTH_SHORT).show();
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this,R.string.correctToast,Toast.LENGTH_SHORT).show();
                checkAnswer(false);
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                //int question = mQuestionBank[mCurrentIndex].getTextResId();
                //mQuestionTextView.setText(question);
                mIsCheater=false;
                updateQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Start CheatActivity
                boolean answerIsTrue=mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent i=CheatActivity.newIntent(MainActivity.this,answerIsTrue);
                startActivityForResult(i,REQUEST_CODE_CHEAT);
            }
        });

        if(savedInstanceState!=null){
            mCurrentIndex= savedInstanceState.getInt(KEY_INDEX,0);
        }

        updateQuestion();


        //Bottom Navigation Function this is to implement the menu of the navigation into main activity
        BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
    }

    //This will allow the main activity to show the menu button on the top
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                //If user click on menu in sign out will let the firebase sign out
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, login.class);
                startActivity(intent);
                break;

            case R.id.item2:
                //If user click on menu in home will let the user back to home page
                FirebaseAuth.getInstance().signOut();
                Intent intent1 = new Intent(this, Home.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //This is to try to load the crime fragment
    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    //This is the bottom navigation bar option if user has click on the crime activity
    //it will goes to Crime Fragment

   @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

       Fragment fragment = null;

        switch(menuItem.getItemId()) {
          //  case R.id.navigation_crime:
            //    fragment = new CrimeFragment();
              //  break;

       }
        return loadFragment(fragment);
    }
}