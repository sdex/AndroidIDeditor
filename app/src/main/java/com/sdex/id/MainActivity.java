package com.sdex.id;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.sdex.id.util.AppUtils;
import com.sdex.id.xml.SettingsFile;
import com.sdex.id.xml.SettingsItem;

public class MainActivity extends AppCompatActivity
  implements IdInputDialog.OnValueInputDialogCallback {

  private static final String STATE_SEARCH_TEXT = "state_search_text";

  private AppListAdapter adapter;
  private SettingsFile settingsFile;
  private String searchText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (savedInstanceState != null) {
      searchText = savedInstanceState.getString(STATE_SEARCH_TEXT);
    }

    RecyclerView recyclerView = findViewById(R.id.list);
    registerForContextMenu(recyclerView);

    final Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.list_divider);
    if (dividerDrawable != null) {
      DividerItemDecoration dividerItemDecoration =
        new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
      dividerItemDecoration.setDrawable(dividerDrawable);
      recyclerView.addItemDecoration(dividerItemDecoration);
    }

    View emptyView = findViewById(R.id.empty);
    View progressView = findViewById(R.id.progressBar);

    MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    viewModel.getPackages().observe(this, settingsFile -> {
      this.settingsFile = settingsFile;
      progressView.setVisibility(View.GONE);
      if (settingsFile != null && settingsFile.getItems() != null) {
        adapter = new AppListAdapter(settingsFile.getItems());
        adapter.filter(searchText);
        recyclerView.setAdapter(adapter);
        adapter.setCallback(position -> {
          SettingsItem item = adapter.getItem(position);
          if (item.canChange()) {
            IdInputDialog dialog = IdInputDialog.newInstance(item);
            dialog.show(getSupportFragmentManager(), IdInputDialog.TAG);
          } else {
            Toast.makeText(this, R.string.error_change_system_android_id,
              Toast.LENGTH_SHORT).show();
          }
        });
        emptyView.setVisibility(View.GONE);
      } else {
        emptyView.setVisibility(View.VISIBLE);
      }
    });

    ProgressViewModel progressViewModel =
      ViewModelProviders.of(this).get(ProgressViewModel.class);
    progressViewModel.getProgress().observe(this, status -> {
      if (status != null) {
        if (status == ProgressViewModel.PROGRESS_FINISH) {
          View content = findViewById(R.id.content);
          Snackbar.make(content, R.string.dialog_finish_message, Snackbar.LENGTH_LONG).show();
          // TODO Shell.Async.su("/system/bin/reboot");
          // Runtime.getRuntime().exec(new String[]{"/system/bin/su","-c","reboot now"});
        }
      }
    });
  }

  @Override
  public void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(STATE_SEARCH_TEXT, searchText);
  }

  @Override
  public boolean onContextItemSelected(MenuItem menuItem) {
    int itemId = menuItem.getItemId();
    final int position = adapter.getContextMenuItemPosition();
    SettingsItem item = adapter.getItem(position);
    String packageName = item.getPackageName();
    if (itemId == AppListAdapter.MENU_ITEM_CHANGE_ID) {
      IdInputDialog dialog = IdInputDialog.newInstance(item);
      dialog.show(getSupportFragmentManager(), IdInputDialog.TAG);
    } else if (itemId == AppListAdapter.MENU_ITEM_OPEN_INFO) {
      AppUtils.openApplicationInfo(MainActivity.this, packageName);
    }
    return super.onContextItemSelected(menuItem);
  }

  @Override
  public void onValueSet(String newId, String newDefaultId, String packageName) {
    MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
    ProgressViewModel progressViewModel =
      ViewModelProviders.of(this).get(ProgressViewModel.class);
    viewModel.setAndroidId(progressViewModel, settingsFile, packageName, newId, newDefaultId);
  }

  private void filter(String text) {
    this.searchText = text;
    if (adapter != null) {
      adapter.filter(searchText);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);
    boolean result = super.onCreateOptionsMenu(menu);
    configureSearch(menu);
    return result;
  }

  private void configureSearch(Menu menu) {
    MenuItem searchItem = menu.findItem(R.id.action_search);
    SearchView searchView = (SearchView) searchItem.getActionView();
    String hint = getString(R.string.action_search_hint);
    searchView.setQueryHint(hint);

    if (!TextUtils.isEmpty(searchText)) {
      searchView.post(() -> searchView.setQuery(searchText, false));
      searchItem.expandActionView();
    }

    searchView.setOnQueryTextListener(new OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        return false;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        filter(newText);
        return false;
      }
    });
  }
}
