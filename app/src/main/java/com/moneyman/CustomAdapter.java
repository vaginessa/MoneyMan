package com.moneyman;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

/**
 * Created by chatRG.
 */


public class CustomAdapter extends BaseAdapter {
    private List<SpentItem> itemList;
    private Context context;

    private static LayoutInflater inflater = null;

    public CustomAdapter(Context context, List<SpentItem> itemList) {
        this.itemList = itemList;
        this.context = context;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Context getContext() {
        return context;
    }

    public class Holder {
        TextView tv_amount;
        TextView tv_desc;
        TextView tv_trans;
        TextView tv_date;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.spent_item, null);

        holder.tv_amount = (TextView) rowView.findViewById(R.id.amount);
        holder.tv_desc = (TextView) rowView.findViewById(R.id.desc);
        holder.tv_trans = (TextView) rowView.findViewById(R.id.trans);
        holder.tv_date = (TextView) rowView.findViewById(R.id.date);

        holder.tv_amount.setText(itemList.get(position).getAmount());
        holder.tv_desc.setText(itemList.get(position).getDesc());
        holder.tv_trans.setText(itemList.get(position).getTransaction());
        holder.tv_date.setText(itemList.get(position).getDate());

        int color;
        if (itemList.get(position).getTransaction().equals(CustomConstants.KEY_CREDIT)) {
            color = R.color.credit;
        } else {
            color = R.color.debit;
        }
        holder.tv_amount.setTextColor(ContextCompat.getColor(getContext(), color));
        holder.tv_trans.setTextColor(ContextCompat.getColor(getContext(), color));

        rowView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog dialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.edit_transaction)
                        .customView(R.layout.dialog_add, true)
                        .neutralText(android.R.string.cancel)
                        .positiveText(android.R.string.ok)
                        .negativeText(CustomConstants.BTN_DELETE)
                        .itemsGravity(GravityEnum.CENTER)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog,
                                                @NonNull DialogAction which) {

                                String amt = ((EditText)
                                        dialog.findViewById(R.id.add_amount)).getText().toString();
                                holder.tv_amount.setText(amt);

                                String dsc = ((EditText)
                                        dialog.findViewById(R.id.add_desc)).getText().
                                        toString().trim();
                                holder.tv_desc.setText(dsc);

                                String dat = ((EditText)
                                        dialog.findViewById(R.id.add_date)).getText().
                                        toString();
                                holder.tv_date.setText(dat);

                                String typ = ((Spinner)
                                        dialog.findViewById(R.id.add_spinner))
                                        .getSelectedItem().toString();
                                holder.tv_trans.setText(typ);

                                notifyDataSetChanged();

                                new DatabaseHandler(getContext())
                                        .updateTransaction(new SpentItem(
                                                itemList.get(position).getId(), amt, dsc, typ, dat));
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new DatabaseHandler(getContext())
                                        .deleteNote(itemList.get(position).getId());
                                String temp = Utils.calculate(itemList.get(position).getAmount(),
                                        Utils.getTotal(getContext()),
                                        itemList.get(position).getTransaction(), 1);
                                Utils.setTotal(getContext(), temp);
                                itemList.remove(itemList.get(position));
                                notifyDataSetChanged();
                            }
                        })
                        .cancelable(false)
                        .build();

                EditText et;

                et = (EditText) dialog.findViewById(R.id.add_amount);
                et.setText(holder.tv_amount.getText());

                et = (EditText) dialog.findViewById(R.id.add_desc);
                et.setText(holder.tv_desc.getText());

                et = (EditText) dialog.findViewById(R.id.add_date);
                et.setText(holder.tv_date.getText());

                Spinner sp = (Spinner) dialog.findViewById(R.id.add_spinner);
                sp.setSelection(holder.tv_trans.getText().toString()
                        .equals(CustomConstants.KEY_CREDIT) ? 0 : 1);

                dialog.show();
            }
        });

        return rowView;
    }
}