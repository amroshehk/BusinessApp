package com.digits.business.classes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.digits.business.R;
import com.digits.business.adapter.RecyclerLogCardAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private RecyclerLogCardAdapter mAdapter;
    private Drawable icon;
    private Drawable icon_call;
    private final ColorDrawable background;
    private final ColorDrawable background_green;

    public SwipeToDeleteCallback(RecyclerLogCardAdapter adapter, Context context) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        icon = ContextCompat.getDrawable(context,
                android.R.drawable.ic_menu_delete);
        icon_call = ContextCompat.getDrawable(context,
                android.R.drawable.ic_menu_call);
        background = new ColorDrawable(Color.RED);
        background_green = new ColorDrawable(Color.GREEN);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        if(direction==ItemTouchHelper.LEFT)
        mAdapter.deleteItem(position);
        else if (direction ==ItemTouchHelper.RIGHT)
            mAdapter.callnumberItem(position);

    }
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX,
                dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundCornerOffset = 20;

        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + icon.getIntrinsicHeight();

        if (dX > 0) { // Swiping to the right
            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
            int iconRight = itemView.getLeft() + iconMargin;
            icon_call.setBounds(iconRight, iconTop, iconLeft, iconBottom);

            background_green.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset,
                    itemView.getBottom());
        } else if (dX < 0) { // Swiping to the left
            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
            int iconRight = itemView.getRight() - iconMargin;
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
        } else { // view is unSwiped
            background.setBounds(0, 0, 0, 0);
            background_green.setBounds(0, 0, 0, 0);
        }

        background.draw(c);
        background_green.draw(c);
        icon.draw(c);
        icon_call.draw(c);
    }


}