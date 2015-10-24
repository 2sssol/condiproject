package condi.kr.ac.swu.condiproject.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import condi.kr.ac.swu.condiproject.R;
import condi.kr.ac.swu.condiproject.data.NetworkAction;
import condi.kr.ac.swu.condiproject.data.Session;


/*
* 초대 받았는지 안받았는지 확인
* */
public class CheckInviteActivity extends RootActivity {

    private boolean isDialogShow = false;
    private boolean isOk = false;
    private String senderName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_invite);
        initActionBar("초대 확인");
        checkInvite();
    }

    private void checkInvite() {
        new AsyncTask() {
            @Override
            protected String doInBackground(Object[] params) {
                String dml = "select m.nickname from invite i, member m where i.sender!='"+ Session.ID+"' and i.receiver='"+Session.ID+"' and i.sender=m.id";
                return NetworkAction.sendDataToServer("checkInvite.php", dml);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                senderName = o.toString();
                if(senderName.equals("error"))
                    showRoomDialog();
                else
                    showInvitedDialog();
            }
        }.execute();
    }

    /*
    * 방 만드는 다이어로그
    * */
    public void showRoomDialog() {

        final Dialog dialog = new Dialog(CheckInviteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(850,450);

        TextView dlgDefaultText_big = (TextView) dialog.findViewById(R.id.dlgDefaultText_big);
        TextView dlgDefaultText_small = (TextView)findViewById(R.id.dlgDefaultText_small);
        Button dlgOk = (Button) dialog.findViewById(R.id.dlgOk);

        dlgDefaultText_big.setText("초대받은 메시지가 없습니다.");
        dlgDefaultText_small.setText("친구를 초대해 보세요.");
        dlgOk.setText("확   인");

        dlgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //System.out.println("방만들기");
                redirectPreGroupActivity(true);
            }
        });

        dialog.show();
        isDialogShow = true;
    }


    /*
    * 초대 수락 거절 다이어로그
    * */
    public void showInvitedDialog() {

        final Dialog dialog = new Dialog(CheckInviteActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_default_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(850,450);

        TextView customDlgTxt_big = (TextView) dialog.findViewById(R.id.customDlgTxt_big);
        Button customDlgBtnOk = (Button) dialog.findViewById(R.id.customDlgBtnOk);
        Button customDlgBtnNo = (Button) dialog.findViewById(R.id.customDlgBtnNo);

        customDlgTxt_big.setText(senderName+" 님이 초대하셨습니다.");
        customDlgBtnOk.setText("수   락");
        customDlgBtnNo.setText("거   절");

        customDlgBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
                isOk = true;
            }
        });

        customDlgBtnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isDialogShow = false;
                isOk = false;
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(isOk)
                    redirectPreGroupActivity(false);
                else
                    redirectPreGroupActivity(true);
            }
        });

        dialog.show();
        isDialogShow = true;
    }

    private void redirectPreGroupActivity(boolean isSender) {
        Intent i = new Intent(CheckInviteActivity.this.getApplicationContext(), PreGroupActivity.class);
        i.putExtra("mode", isSender);
        startActivity(i);
        finish();
    }
}
