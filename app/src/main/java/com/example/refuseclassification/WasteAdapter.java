package com.example.refuseclassification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WasteAdapter extends RecyclerView.Adapter<WasteAdapter.ViewHolder> {

  private final List<WasteItem> wasteItems;

  public WasteAdapter(List<WasteItem> wasteItems) {
    this.wasteItems = wasteItems;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waste_item_layout, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(ViewHolder holder, int position) {
    WasteItem item = wasteItems.get(position);
    holder.nameTextView.setText(item.getName());
    holder.typeTextView.setText(item.getTypeName());
    holder.indexTextView.setText("热度: " + item.getIndex());
  }

  @Override
  public int getItemCount() {
    return wasteItems.size();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    public TextView nameTextView;
    public TextView typeTextView;
    public TextView indexTextView;

    public ViewHolder(View itemView) {
      super(itemView);
      nameTextView = itemView.findViewById(R.id.name);
      typeTextView = itemView.findViewById(R.id.type);
      indexTextView = itemView.findViewById(R.id.index);
    }
  }
}
