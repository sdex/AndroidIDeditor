package com.sdex.id;

import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sdex.id.xml.SettingsItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.ViewHolder> {

  public static final int MENU_ITEM_CHANGE_ID = 0;
  public static final int MENU_ITEM_OPEN_INFO = 1;

  private final List<SettingsItem> items;
  private final List<SettingsItem> allItems;
  private Callback callback;
  private int contextMenuItemPosition;

  public AppListAdapter(List<SettingsItem> items) {
    this.allItems = items;
    this.items = new ArrayList<>(items);
  }

  @NonNull
  @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = inflater.inflate(R.layout.item_application, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    SettingsItem item = items.get(position);
    holder.bind(item);
    holder.itemView.setOnClickListener(v -> {
      setContextMenuItemPosition(holder.getAdapterPosition());
      if (callback != null) {
        callback.onItemSelected(holder.getAdapterPosition());
      }
    });
    holder.itemView.setOnLongClickListener(v -> !item.canChange());
  }

  @Override
  public int getItemCount() {
    return items.size();
  }

  public int getContextMenuItemPosition() {
    return contextMenuItemPosition;
  }

  public void setContextMenuItemPosition(int contextMenuItemPosition) {
    this.contextMenuItemPosition = contextMenuItemPosition;
  }

  public SettingsItem getItem(int position) {
    return items.get(position);
  }

  public void setCallback(Callback callback) {
    this.callback = callback;
  }

  public void filter(String searchText) {
    items.clear();
    if (TextUtils.isEmpty(searchText)) {
      items.addAll(allItems);
    } else {
      for (SettingsItem item : allItems) {
        if (item.getAppName().toLowerCase().contains(searchText.toLowerCase())) {
          items.add(item);
        }
      }
    }
    //noinspection Java8ListSort
    Collections.sort(items, (o1, o2) -> {
      if (o1.getAppName() == null) {
        return -1;
      }
      if (o2.getAppName() == null) {
        return 1;
      }
      return o1.getAppName().compareToIgnoreCase(o2.getAppName());
    });
    notifyDataSetChanged();
  }

  interface Callback {

    void onItemSelected(int position);
  }

  class ViewHolder extends RecyclerView.ViewHolder
    implements View.OnCreateContextMenuListener {

    private final ImageView appIcon;
    private final TextView appName;
    private final TextView appPackageName;
    private final TextView idValue;

    public ViewHolder(View itemView) {
      super(itemView);
      appIcon = itemView.findViewById(R.id.app_icon);
      appName = itemView.findViewById(R.id.app_name);
      appPackageName = itemView.findViewById(R.id.app_package_name);
      idValue = itemView.findViewById(R.id.id_value);
    }

    public void bind(SettingsItem item) {
      itemView.setOnCreateContextMenuListener(this);

      appName.setText(item.getAppName());
      appPackageName.setText(item.getPackageName());
      idValue.setText(item.getValue());

      PackageManager packageManager = itemView.getContext().getPackageManager();
      try {
        Drawable icon = packageManager.getApplicationIcon(item.getPackageName());
        appIcon.setImageDrawable(icon);
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      SettingsItem item = getItem(getAdapterPosition());
      menu.setHeaderTitle(item.getAppName());
      menu.add(Menu.NONE, MENU_ITEM_CHANGE_ID, Menu.NONE,
        R.string.options_menu_change_id);
      menu.add(Menu.NONE, MENU_ITEM_OPEN_INFO, Menu.NONE,
        R.string.options_menu_open_app_info);
    }

  }
}
