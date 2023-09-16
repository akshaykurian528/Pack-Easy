package com.example.pack_easy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.pack_easy.Adapter.CheckListAdaptor;
import com.example.pack_easy.Constants.MyConstants;
import com.example.pack_easy.Data.AppData;
import com.example.pack_easy.Database.RoomDB;
import com.example.pack_easy.Models.Items;

import java.util.ArrayList;
import java.util.List;



public class CheckList extends AppCompatActivity {

    RecyclerView recyclerView;
    CheckListAdaptor checkListAdaptor;
    RoomDB database;
    List<Items> itemsList = new ArrayList<>();
    String header, show;

    EditText txtAdd;
    Button btnAdd;
    LinearLayout linearLayout;

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_one, menu);

        if (MyConstants.MY_SELECTIONS.equals(header)){
            menu.getItem(0).setVisible(false);
            menu.getItem(2).setVisible(false);
            menu.getItem(3).setVisible(false);
        } else if (MyConstants.MY_LIST_CAMEL_CASE.equals(header))
            menu.getItem(1).setVisible(false);

        MenuItem menuItem = menu.findItem(R.id.btnSearch);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                List<Items> mFinalList = new ArrayList<>();
                for (Items items:itemsList){
                    if (items.getItemname().toLowerCase().startsWith(s.toLowerCase())){
                        mFinalList.add(items);
                    }
                }
                updateRecycler(mFinalList);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(this, CheckList.class);
        AppData appData = new AppData(database, this);

        if (item.getItemId() == R.id.btnMySelections) {
            // Handle btnMySelections
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_SELECTIONS);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.FALSE_STRING);
            startActivityForResult(intent, 101);
            return true;
        } else if (item.getItemId() == R.id.btnCustomList) {
            // Handle btnCustomList
            intent.putExtra(MyConstants.HEADER_SMALL, MyConstants.MY_LIST_CAMEL_CASE);
            intent.putExtra(MyConstants.SHOW_SMALL, MyConstants.TRUE_STRING);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.btnDeleteDefault) {
            // Handle btnDeleteDefault
            new AlertDialog.Builder(this)
                    .setTitle("Delete default data")
                    .setMessage("Are you Sure?\n\n All data will be deleted")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appData.persistDataByCategory(header, true);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Handle cancel button click
                        }
                    })
                    .setIcon(R.drawable.alert_ic)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.btnReset) {
            // Handle btnReset
            new AlertDialog.Builder(this)
                    .setTitle("Reset to Default")
                    .setMessage("Are you Sure?\n\n This will delete the custom data that you have added in and load the default data in ("+ header +")")
                    .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            appData.persistDataByCategory(header, false);
                            itemsList = database.mainDao().getAll(header);
                            updateRecycler(itemsList);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Handle cancel button click
                        }
                    })
                    .setIcon(R.drawable.alert_ic)
                    .show();
            return true;
        } else if (item.getItemId() == R.id.btnExit) {
            this.finishAffinity();
            Toast.makeText(this, "Pack-Easy\nExit Completed", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==101){
            itemsList = database.mainDao().getAll(header);
            updateRecycler(itemsList);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckeck_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        header = intent.getStringExtra(MyConstants.HEADER_SMALL);
        show = intent.getStringExtra(MyConstants.SHOW_SMALL);

        getSupportActionBar().setTitle(header);

        txtAdd = findViewById(R.id.txtAdd);
        btnAdd = findViewById(R.id.btnAdd);
        recyclerView = findViewById(R.id.recyclerview);
        linearLayout = findViewById(R.id.linearLayout);

        database = RoomDB.getInstance(this);

        if(MyConstants.FALSE_STRING.equals(show)) {
            linearLayout.setVisibility(View.GONE);
            itemsList = database.mainDao().getAllSelected(true);
        }else{
            itemsList = database.mainDao().getAll(header);
        }

        updateRecycler(itemsList);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemName = txtAdd.getText().toString();
                if(itemsList!=null && !itemName.isEmpty()){
                    addNewItem(itemName);
                    Toast.makeText(CheckList.this, "Item added", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CheckList.this, "Empty cant be added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void addNewItem (String itemName){
        Items item = new Items();
        item.setChecked(false);
        item.setCategory(header);
        item.setItemname(itemName);
        item.setAddedby(MyConstants.USER_SMALL);
        database.mainDao().saveItem(item);
        itemsList = database.mainDao().getAll(header);
        updateRecycler(itemsList);
        recyclerView.scrollToPosition(checkListAdaptor.getItemCount() - 1);
        txtAdd.setText("");
    }

    private void updateRecycler(List<Items> itemsList){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,LinearLayoutManager.VERTICAL));
        checkListAdaptor = new CheckListAdaptor(CheckList.this,itemsList,database,show);
        recyclerView.setAdapter(checkListAdaptor);
    }
}