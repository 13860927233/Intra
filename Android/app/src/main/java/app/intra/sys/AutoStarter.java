/*
Copyright 2018 Jigsaw Operations LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package app.intra.sys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.util.Log;
import app.intra.ui.MainActivity;
import com.crashlytics.android.Crashlytics;

/**
 * Broadcast receiver that runs on boot, and also when the app is restarted due to an update.
 */
public class AutoStarter extends BroadcastReceiver {
  private static final String LOG_TAG = "AutoStarter";

  @Override
  public void onReceive(Context context, Intent intent) {
    Crashlytics.log(Log.DEBUG, LOG_TAG, "Boot event");
    VpnController controller = VpnController.getInstance();
    VpnState state = controller.getState(context);
    if (state.activationRequested && !state.on) {
      Crashlytics.log(Log.DEBUG, LOG_TAG, "Autostart enabled");
      if (VpnService.prepare(context) != null) {
        // prepare() returns a non-null intent if VPN permission has not been granted.
        Crashlytics.log(Log.WARN, LOG_TAG, "VPN permission not granted.  Starting UI.");
        Intent startIntent = new Intent(context, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
        return;
      }
      controller.start(context);
    }
  }
}
