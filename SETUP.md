# Expense Tracker - Setup Guide

This guide will help you build and run the Expense Tracker Android app.

## Prerequisites

1. **Android Studio** (Arctic Fox or newer)
   - Download from: https://developer.android.com/studio
   - Install with default settings

2. **Java Development Kit (JDK) 17**
   - Usually bundled with Android Studio
   - Verify: Open Terminal and run `java -version`

3. **Android SDK**
   - Android SDK 34 (Android 14)
   - SDK Build Tools 34.0.0
   - Android Emulator (optional, for testing)

## Step-by-Step Setup

### 1. Open the Project

1. Launch Android Studio
2. Select **File > Open**
3. Navigate to `D:\Projects\ExpenseTracker`
4. Click **OK**

### 2. Configure SDK Location

If you see "SDK location not found", you need to set it:

1. Open `local.properties` in the project root
2. Update the SDK path:
   ```
   sdk.dir=C\:\\Users\\YourUsername\\AppData\\Local\\Android\\Sdk
   ```
   Replace `YourUsername` with your actual Windows username

### 3. Sync Gradle

1. Android Studio will prompt you to sync Gradle
2. Click **Sync Now**
3. Wait for the sync to complete (may take several minutes on first run)
4. If you see any errors, try **File > Invalidate Caches > Invalidate and Restart**

### 4. Build the Project

1. Go to **Build > Make Project** (or press `Ctrl+F9`)
2. Wait for the build to complete
3. Check the Build Output panel for any errors

### 5. Run the App

#### Option A: On an Emulator

1. Go to **Tools > Device Manager**
2. Create a new virtual device:
   - Click **Create Device**
   - Select a phone (e.g., Pixel 6)
   - Select System Image: **Android 14 (API 34)**
   - Click **Finish**
3. Click **Run > Run 'app'** (or press `Shift+F10`)
4. Select your emulator from the list
5. Click **OK**

#### Option B: On a Physical Device

1. Enable Developer Options on your Android phone:
   - Go to **Settings > About Phone**
   - Tap **Build Number** 7 times
   - Go back to Settings > **Developer Options**
   - Enable **USB Debugging**

2. Connect your phone via USB

3. When prompted on your phone, allow USB debugging

4. In Android Studio, click **Run > Run 'app'**

5. Select your device from the list

6. Click **OK**

## First Run Configuration

### Grant Permissions

When the app launches for the first time, it will request permissions:

1. **SMS Permissions**
   - Tap **Allow** to enable automatic transaction detection
   - Required for reading bank SMS

2. **Notification Permissions** (Android 13+)
   - Tap **Allow** to receive alerts and reminders

### Configure Your Billing Cycle

1. Tap the **Settings** icon (top-right)
2. Tap **Cycle Start Day**
3. Enter the day of month when your billing cycle starts (1-31)
4. Tap **Save**

### Set Up Budgets

1. Return to the Dashboard
2. Tap **Set Up Your Budgets**
3. Enter budget amounts for each category:
   - Food
   - Transport
   - Kids
   - Bills
   - Savings
   - Other
4. Tap **Save Budgets**

## Testing SMS Detection

To test automatic SMS detection without actual bank transactions:

1. You can use an SMS testing app or send yourself a test SMS
2. Example bank SMS format:
   ```
   Card txn: Rs.1,250.00 spent on Card ending 5678
   Available balance: Rs.45,320.50
   ```

3. The app should detect it and send a notification

## Common Issues and Solutions

### Issue: Gradle Sync Failed

**Solution:**
- Check internet connection
- Try **File > Sync Project with Gradle Files**
- Update Gradle wrapper: `./gradlew wrapper --gradle-version=8.2`

### Issue: SDK Not Found

**Solution:**
- Open **Tools > SDK Manager**
- Install Android SDK 34
- Update `local.properties` with correct SDK path

### Issue: Build Errors

**Solution:**
- Clean project: **Build > Clean Project**
- Rebuild: **Build > Rebuild Project**
- Invalidate caches: **File > Invalidate Caches > Invalidate and Restart**

### Issue: App Crashes on Launch

**Solution:**
- Check logcat for error messages: **View > Tool Windows > Logcat**
- Ensure minimum SDK 26 device/emulator
- Verify all permissions are granted

### Issue: SMS Not Detected

**Solution:**
- Grant SMS permissions: **Settings > Apps > Expense Tracker > Permissions**
- Check if SMS pattern matches your bank's format
- Review logcat for parsing errors

### Issue: Notifications Not Working

**Solution:**
- Grant notification permission
- Check phone notification settings
- Disable battery optimization for the app

## Project Structure Overview

```
ExpenseTracker/
├── app/
│   ├── build.gradle.kts          # App-level build configuration
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml
│   │       ├── java/com/expense/tracker/
│   │       │   ├── data/         # Database, DAOs, Repository
│   │       │   ├── domain/       # Business logic models
│   │       │   ├── receiver/     # SMS receiver
│   │       │   ├── ui/           # Compose UI screens
│   │       │   ├── utils/        # Helper classes
│   │       │   └── worker/       # Background tasks
│   │       └── res/              # Resources (strings, themes)
├── build.gradle.kts              # Project-level build config
├── settings.gradle.kts           # Gradle settings
├── gradle/                       # Gradle wrapper files
├── local.properties              # Local SDK configuration
└── README.md                     # Project documentation
```

## Development Tips

1. **Hot Reload**: Jetpack Compose supports live preview
   - Open any `@Composable` function
   - Click **Split** view to see live preview

2. **Database Inspector**:
   - **View > Tool Windows > App Inspection**
   - Select **Database Inspector**
   - Explore Room database in real-time

3. **Logcat Filtering**:
   - Use filters like `tag:SmsReceiver` to debug SMS detection
   - Use `tag:ExpenseTracker` for general app logs

4. **Testing Categories**:
   - Categories are auto-populated on first launch
   - Check DatabaseCallback in ExpenseDatabase.kt

## Building Release APK

1. Go to **Build > Generate Signed Bundle / APK**
2. Select **APK**
3. Create a new keystore or use existing
4. Fill in keystore details
5. Select **release** build variant
6. Click **Finish**
7. APK will be generated in `app/release/`

## Next Steps

- Read `README.md` for feature details
- Explore the code structure
- Customize SMS parsing for your bank
- Add new categories or subcategories
- Implement pie chart visualization

## Support

For detailed feature documentation, see `README.md`

For architecture details, see the inline code comments
