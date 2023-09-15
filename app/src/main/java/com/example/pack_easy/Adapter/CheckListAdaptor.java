    package com.example.pack_easy.Adapter;

    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.graphics.Color;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.CheckBox;
    import android.widget.LinearLayout;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;
    import androidx.room.Delete;

    import com.example.pack_easy.Constants.MyConstants;
    import com.example.pack_easy.Database.RoomDB;
    import com.example.pack_easy.Models.Items;
    import com.example.pack_easy.R;

    import java.util.List;

    public class CheckListAdaptor extends RecyclerView.Adapter<CheckListViewHolder> {

        Context context;
        List<Items> itemsList;
        RoomDB database;
        String show;

        public CheckListAdaptor() {
        }

        public CheckListAdaptor(Context context, List<Items> itemsList, RoomDB database, String show) {
            this.context = context;
            this.itemsList = itemsList;
            this.database = database;
            this.show = show;
            if(itemsList.size()==0)
                Toast.makeText(context.getApplicationContext(), "Emptyyy", Toast.LENGTH_SHORT).show();
        }

        @NonNull
        @Override
        public CheckListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new CheckListViewHolder(LayoutInflater.from(context).inflate(R.layout.check_list_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull CheckListViewHolder holder, int position) {
            holder.checkBox.setText(itemsList.get(position).getItemname());
            holder.checkBox.setChecked(itemsList.get(position).getChecked());

            if(MyConstants.FALSE_STRING.equals(show)) {
                holder.btnDelete.setVisibility(View.GONE);
                holder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
            } else {
                if (itemsList.get(position).getChecked()) {
                    holder.layout.setBackgroundColor(Color.parseColor("#8e546f"));
                } else
                    holder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.border_one));
            }

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Boolean check = holder.checkBox.isChecked();
                    database.mainDao().checkUncheck(itemsList.get(position).getID(),check);
                    if (MyConstants.FALSE_STRING.equals(show)) {
                        itemsList = database.mainDao().getAllSelected(true);
                        notifyDataSetChanged();
                    }
                    else{
                        itemsList.get(position).setChecked(check);
                        notifyDataSetChanged();
                        Toast tostMessage = null;
                        if (tostMessage!=null){
                            tostMessage.cancel();
                        }
                        if (itemsList.get(position).getChecked()){
                            tostMessage = Toast.makeText(context, "(" + holder.checkBox.getText() + ") Packed", Toast.LENGTH_SHORT);
                        }else{
                            tostMessage = Toast.makeText(context, "(" + holder.checkBox.getText() + ") Un-Packed", Toast.LENGTH_SHORT);
                        }
                        tostMessage.show();
                    }
                }
            });

            holder.btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle("Delete( "+itemsList.get(position).getItemname()+" )")
                            .setMessage("Are you Sure ?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    database.mainDao().delete(itemsList.get(position));
                                    itemsList.remove(itemsList.get(position));
                                    notifyDataSetChanged();
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            }).setIcon(R.drawable.delete_forever)
                            .show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return itemsList.size();
        }
    }

    class CheckListViewHolder extends RecyclerView.ViewHolder{

        LinearLayout layout;
        CheckBox checkBox;
        Button btnDelete;
        public CheckListViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.linearLayout);
            checkBox = itemView.findViewById(R.id.checkbox);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
