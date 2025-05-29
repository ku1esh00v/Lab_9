package com.example.lab_9;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private GridView gameGrid;
    private GridAdapter gridAdapter;
    private int currentGridSize = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameGrid = findViewById(R.id.game_grid);
        gridAdapter = new GridAdapter(this, currentGridSize);
        gameGrid.setAdapter(gridAdapter);
        registerForContextMenu(gameGrid);

        setupSizeButtons();
    }

    private void setupSizeButtons() {
        int[] buttonIds = {R.id.btn_3x3, R.id.btn_4x4, R.id.btn_5x5};
        int[] sizes = {3, 4, 5};

        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            int size = sizes[i];

            button.setOnClickListener(v -> {
                currentGridSize = size;
                updateGridSize();
            });
        }
    }

    private void updateGridSize() {
        gridAdapter = new GridAdapter(this, currentGridSize);
        gameGrid.setAdapter(gridAdapter);
        gameGrid.setNumColumns(currentGridSize);
        gameGrid.invalidateViews();
        gameGrid.requestLayout();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.grid_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        if (item.getItemId() == R.id.delete_item) {
            gridAdapter.deleteItem(position);
            return true;
        } else if (item.getItemId() == R.id.change_color) {
            gridAdapter.changeColor(position);
            return true;
        }
        return super.onContextItemSelected(item);
    }

    class GridAdapter extends BaseAdapter {
        private Context context;
        private int gridSize;
        private List<Integer> colors;
        private List<Boolean> visibleItems;
        private Random random = new Random();

        public GridAdapter(Context context, int gridSize) {
            this.context = context;
            this.gridSize = gridSize;
            this.colors = new ArrayList<>();
            this.visibleItems = new ArrayList<>();
            generateColors();
        }

        private void generateColors() {
            colors.clear();
            visibleItems.clear();
            for (int i = 0; i < gridSize * gridSize; i++) {
                colors.add(Color.rgb(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                ));
                visibleItems.add(true);
            }
        }

        public void deleteItem(int position) {
            if (position >= 0 && position < visibleItems.size()) {
                visibleItems.set(position, false);
                notifyDataSetChanged();
                Toast.makeText(context, "Удален элемент " + (position + 1), Toast.LENGTH_SHORT).show();
            }
        }

        public void changeColor(int position) {
            if (position >= 0 && position < colors.size() && visibleItems.get(position)) {
                colors.set(position, Color.rgb(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256)
                ));
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            int count = 0;
            for (Boolean visible : visibleItems) {
                if (visible) count++;
            }
            return count;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Получаем реальную позицию в списке
            int realPosition = getRealPosition(position);

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.grid_item, parent, false);
            }

            View container = convertView.findViewById(R.id.grid_item_container);
            TextView textView = convertView.findViewById(R.id.grid_item_text);

            if (realPosition != -1) {
                container.setBackgroundColor(colors.get(realPosition));
                textView.setText(String.valueOf(realPosition + 1));
                container.setVisibility(View.VISIBLE);
            } else {
                container.setVisibility(View.GONE);
            }

            return convertView;
        }

        private int getRealPosition(int visiblePosition) {
            int count = -1;
            for (int i = 0; i < visibleItems.size(); i++) {
                if (visibleItems.get(i)) {
                    count++;
                    if (count == visiblePosition) {
                        return i;
                    }
                }
            }
            return -1;
        }
    }
}