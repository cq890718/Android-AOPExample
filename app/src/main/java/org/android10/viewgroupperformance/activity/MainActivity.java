package org.android10.viewgroupperformance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.android10.gintonic.annotation.DebugAfter;
import org.android10.gintonic.annotation.DebugAround;
import org.android10.gintonic.annotation.DebugBefore;
import org.android10.viewgroupperformance.R;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  private Button btnBefore;
  private Button btnAfter;
  private Button btnAround;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mapGUI();
  }

  /**
   * Maps Graphical User Interface
   */
  private void mapGUI() {
    this.btnBefore = (Button) findViewById(R.id.btnBefore);
    this.btnBefore.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        testBefore();
      }
    });

    this.btnAfter = (Button) findViewById(R.id.btnAfter);
    this.btnAfter.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        testAfter();
      }
    });

    this.btnAround = (Button) findViewById(R.id.btnAround);
    this.btnAround.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        testAround();
      }
    });
  }

  @DebugBefore
  private void testBefore() {
    Log.d(TAG, "testBefore running");
  }

  @DebugAfter
  private void testAfter() {
    Log.d(TAG, "testAfter running");
  }

  @DebugAround
  private void testAround() {
    Log.d(TAG, "testAround running");
  }
}
