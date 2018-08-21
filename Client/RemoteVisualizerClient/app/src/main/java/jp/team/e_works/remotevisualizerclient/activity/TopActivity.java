package jp.team.e_works.remotevisualizerclient.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import jp.team.e_works.remotevisualizerclient.R;

public class TopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        Button tempBtn = findViewById(R.id.temp_transBtn);
        tempBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopActivity.this, VisualizerActivity.class);
                startActivity(intent);
            }
        });
    }
}
