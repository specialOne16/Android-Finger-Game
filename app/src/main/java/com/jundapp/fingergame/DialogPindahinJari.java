package com.jundapp.fingergame;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;

public class DialogPindahinJari extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public int hand;
    public Button yes, no, cancel;

    public DialogPindahinJari(Activity a, int hand) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.hand = hand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_pindahin_jari);
        yes = (Button) findViewById(R.id.btn_yes);
        no = (Button) findViewById(R.id.btn_no);
        cancel = (Button) findViewById(R.id.btn_cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                ((MainActivity) c).doMove(hand, 1);
                break;
            case R.id.btn_no:
                ((MainActivity) c).doMove(hand, 2);
                break;
            default:
                ((MainActivity) c).doMove(hand, 0);
                break;
        }
        dismiss();
    }
}
