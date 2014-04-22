package de.theknut.nowirelesschargingsound;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.getObjectField;

import java.text.DateFormat;
import java.util.Calendar;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedHook implements IXposedHookLoadPackage {
	
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		
		if (!lpparam.packageName.equals("android")) return;
		
		try {
			findAndHookMethod("com.android.server.power.Notifier", lpparam.classLoader, "playWirelessChargingStartedSound", new XC_MethodHook() {
				
				@Override
				protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
					
					String timestamp = DateFormat.getTimeInstance(DateFormat.SHORT).format(Calendar.getInstance().getTime());
					XposedBridge.log(timestamp + " No Wireless Charging Sound| Blocking wireless charging sound");
					
					Object mSuspendBlocker = getObjectField(param.thisObject, "mSuspendBlocker");
					callMethod(mSuspendBlocker, "release");
					
					param.setResult(null);
				}
			});
		} catch(NoSuchMethodError nsme) {

			XposedBridge.log("No Wireless Charging Sound| Only Android 4.2.2 and up is supported");
		}
	}
}