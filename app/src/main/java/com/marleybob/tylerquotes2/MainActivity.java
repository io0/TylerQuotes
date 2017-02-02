package com.marleybob.tylerquotes2;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<Block> myBlocks = new ArrayList<>();
    Button quoteButton;
    EditText editQuote;

    DatabaseReference tylerRef = FirebaseDatabase.getInstance().getReference("Tyler");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        quoteButton = (Button) findViewById(R.id.quote_button);
        editQuote = (EditText) findViewById(R.id.edit_quote);
        tylerRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Block theBlock = dataSnapshot.getValue(Block.class);
                myBlocks.add(theBlock);
                initializeListView();
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
    }

    protected void onStart(){
        super.onStart();

        quoteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Block theBlock = new Block();
                theBlock.setDate(df.format(Calendar.getInstance().getTime()));
                theBlock.setQuote(editQuote.getText().toString());
                theBlock.setScore(0);
                DatabaseReference temp = tylerRef.push();
                temp.setValue(theBlock);
                editQuote.setText("");
            }
        });
    }


    private void initializeListView(){
        ArrayAdapter<Block> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.ListView);
        list.setAdapter(adapter);
    }

    private class MyListAdapter extends ArrayAdapter<Block>{
        public MyListAdapter(){
            super (MainActivity.this, R.layout.list_block, myBlocks);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.list_block, null);
            }
            //Find block
            final Block currentBlock = myBlocks.get(position);

            TextView blockScore = (TextView) itemView.findViewById(R.id.block_score);
            blockScore.setText(Integer.toString(currentBlock.getScore()));
            Button blockUpvote = (Button) itemView.findViewById(R.id.block_upvote);
            Button blockDownvote = (Button) itemView.findViewById(R.id.block_downvote);
            //Access the respective score field
            //itemView.setTag(blockScore);
            //final TextView temp = (TextView) itemView.getTag();

            blockUpvote.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    currentBlock.setScore(currentBlock.getScore()+1);
                    Query query = tylerRef.orderByChild("quote").equalTo(currentBlock.getQuote());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                            String key = nodeDataSnapshot.getKey();
                            tylerRef.child(key).setValue(currentBlock);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    initializeListView();
                }
            });

            blockDownvote.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    currentBlock.setScore(currentBlock.getScore()-1);
                    Query query = tylerRef.orderByChild("quote").equalTo(currentBlock.getQuote());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            DataSnapshot nodeDataSnapshot = dataSnapshot.getChildren().iterator().next();
                            String key = nodeDataSnapshot.getKey();
                            tylerRef.child(key).setValue(currentBlock);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    initializeListView();
                }
            });

            //Populate text
            TextView quote = (TextView) itemView.findViewById(R.id.block_quote);
            quote.setText('"' + currentBlock.getQuote() + '"');
            TextView date = (TextView) itemView.findViewById(R.id.block_date);
            date.setText(currentBlock.getDate());

            //Bookman Old Style
            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/BOOKOS.TTF");
            quote.setTypeface(type);
            date.setTypeface(type);

            return itemView;
        }
    }
}
