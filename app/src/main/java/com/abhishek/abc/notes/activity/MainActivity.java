package com.abhishek.abc.notes.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.abhishek.abc.notes.R;
import com.abhishek.abc.notes.adapter.ListAdapter;
import com.abhishek.abc.notes.network.NetworkClient;
import com.abhishek.abc.notes.network.NetworkInterface;
import com.abhishek.abc.notes.network.models.NotesModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = "NOTE_MN_ACTY";
    private CompositeDisposable mDisposable;
    private NetworkInterface mNetworkService;
    private ArrayList<NotesModel> mList;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.empty_notes_placeholder)
    TextView noNotesPlaceholder;

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private ListAdapter mRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initActivity();
        fetchAllNotes();
    }

    private void initActivity() {
        mDisposable = new CompositeDisposable();
        mNetworkService = NetworkClient.getClient(MainActivity.this).create(NetworkInterface.class);

        //Initialiazing recycler view
        mList = new ArrayList<>();
        mRecyclerViewAdapter = new ListAdapter(MainActivity.this,mList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mRecyclerViewAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNotesDialogue(false,null,-1);
            }
        });
    }

    private void showNotesDialogue(final boolean isUpdate, final NotesModel note, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View view = layoutInflater.inflate(R.layout.note_dialogue,null);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView title = view.findViewById(R.id.dialog_title);
        title.setText(!isUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));
        if (isUpdate && note != null) {
            inputNote.setText(note.getNote());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setPositiveButton(isUpdate ? "update" : "save",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // Show toast message when no text is entered
                        if (TextUtils.isEmpty(inputNote.getText().toString())) {
                            Toast.makeText(MainActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            dialogBox.dismiss();
                        }

                        // check if user updating note
                        if (isUpdate && note != null) {
                            // update note by it's id
                            updateNote(note.getId(), inputNote.getText().toString(), position);
                        } else {
                            // create new note
                            createNote(inputNote.getText().toString());
                        }
                    }
                });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        dialogBox.cancel();
                    }
                });
        AlertDialog dialogue = builder.create();
        dialogue.show();

    }

    private void fetchAllNotes() {
        mNetworkService
                .fetchAllNotes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<List<NotesModel>, List<NotesModel>>() {
                    @Override
                    public List<NotesModel> apply(List<NotesModel> notesModels) throws Exception {
                        Collections.sort(notesModels, new Comparator<NotesModel>() {
                            @Override
                            public int compare(NotesModel n1, NotesModel t1) {
                                return t1.getId() - n1.getId();
                            }
                        });
                        return notesModels;
                    }
                })
                .subscribeWith(new DisposableSingleObserver<List<NotesModel>>() {
                    @Override
                    public void onSuccess(List<NotesModel> notesModels) {
                            mList.clear();
                            mList.addAll(notesModels);
                            mRecyclerViewAdapter.notifyDataSetChanged();
                            toggleEmptyNotes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                });
    }

    private void createNote(String note) {
        mDisposable.add(mNetworkService
                .createNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<NotesModel>() {
                    @Override
                    public void onSuccess(NotesModel notesModel) {
                        if (!TextUtils.isEmpty(notesModel.getError())) {
                            Toast.makeText(getApplicationContext(), notesModel.getError(), Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d(TAG, "new note created: " + notesModel.getId()
                                + ", " + notesModel.getNote() + ", " + notesModel.getTimestamp());

                        // Add new item and notify adapter
                        //mList.add(0, notesModel);
                        //mRecyclerViewAdapter.notifyItemInserted(0);
                        toggleEmptyNotes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }
                }));
    }

    private void updateNote(int id, final String noteText, final int position) {
        mDisposable.add(
            mNetworkService.updateNote(id,noteText)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            Log.d(TAG, "Note updated!");
                            NotesModel n = mList.get(position);
                            n.setNote(noteText);
                            // Update item and notify adapter
                            mList.set(position, n);
                            mRecyclerViewAdapter.notifyItemChanged(position);
                        }


                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }
                    })
        );
    }

    private void toggleEmptyNotes() {
        if (mList.size() > 0) {
            noNotesPlaceholder.setVisibility(View.GONE);
        } else {
            noNotesPlaceholder.setVisibility(View.VISIBLE);
        }
    }
}
