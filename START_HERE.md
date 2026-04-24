# 🚀 START HERE - Get Your APK in 10 Minutes

Follow these simple steps to build and install the Expense Tracker app on your phone.

## ✅ What You'll Do

1. Download Gradle Wrapper (1 minute)
2. Create GitHub account (2 minutes)
3. Push code to GitHub (3 minutes)
4. Wait for build (5-10 minutes)
5. Download & install APK (2 minutes)

**Total time**: ~15 minutes (most is waiting for build)

---

## Step 1: Download Gradle Wrapper (Required)

The project needs a small file called `gradle-wrapper.jar` to build on GitHub.

### On Windows:

1. Open Command Prompt
2. Navigate to project:
   ```
   cd D:\Projects\ExpenseTracker
   ```
3. Run the setup script:
   ```
   setup-gradle-wrapper.bat
   ```

### On Mac/Linux:

1. Open Terminal
2. Navigate to project:
   ```
   cd D:/Projects/ExpenseTracker
   ```
3. Run the setup script:
   ```
   chmod +x setup-gradle-wrapper.sh
   ./setup-gradle-wrapper.sh
   ```

### Manual Download (if script fails):

1. Visit: https://raw.githubusercontent.com/gradle/gradle/v8.2.0/gradle/wrapper/gradle-wrapper.jar
2. Right-click → Save As
3. Save to: `D:\Projects\ExpenseTracker\gradle\wrapper\gradle-wrapper.jar`

**✓ Verify**: Check that file exists at `gradle/wrapper/gradle-wrapper.jar`

---

## Step 2: Follow the GitHub Setup Guide

Open and follow: **`GITHUB_SETUP.md`**

It will guide you through:
- Creating a GitHub account (if you don't have one)
- Uploading the project
- Automatic APK building

**Quick Summary:**
```bash
cd D:\Projects\ExpenseTracker
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR-USERNAME/expense-tracker-app.git
git push -u origin main
```

Replace `YOUR-USERNAME` with your actual GitHub username.

---

## Step 3: Download APK from GitHub

Once the build completes:

### Option A: From Actions
1. Go to your repository on GitHub
2. Click **Actions** tab
3. Click the latest workflow (should be green ✓)
4. Scroll down to **Artifacts**
5. Click **expense-tracker-debug** to download
6. Extract the ZIP file
7. Find `app-debug.apk`

### Option B: From Releases (Easier)
1. Go to your repository
2. Click **Releases** (right sidebar)
3. Click the latest release
4. Download `app-debug.apk` directly

---

## Step 4: Install on Your Phone

1. **Transfer APK to phone**:
   - USB cable (copy to Downloads folder)
   - Email it to yourself
   - Upload to Google Drive and download on phone
   - Use any file transfer method

2. **Enable installation**:
   - Settings → Security → Enable "Unknown Sources"
   - Or: Settings → Apps → Special Access → Install Unknown Apps
   - Enable for your file manager

3. **Install**:
   - Open file manager on phone
   - Navigate to Downloads (or where you saved it)
   - Tap `app-debug.apk`
   - Tap **Install**
   - Wait for installation
   - Tap **Open**

---

## Step 5: First-Time Setup

1. **Grant Permissions**:
   - Allow SMS Read/Receive (for auto-detection)
   - Allow Notifications (for reminders)

2. **Set Billing Cycle**:
   - Tap Settings icon
   - Set "Cycle Start Day" (e.g., 25 if salary is on 25th)

3. **Set Budgets**:
   - Return to Dashboard
   - Tap "Set Up Your Budgets"
   - Enter monthly budget for each category
   - Tap Save

4. **Start Tracking**:
   - Make a purchase with your card
   - App will detect the SMS and notify you
   - Tap notification to categorize
   - Done!

---

## 🎯 Quick Reference

| What | Where | Time |
|------|-------|------|
| **Setup Gradle** | Run `setup-gradle-wrapper.bat` | 1 min |
| **GitHub Setup** | Follow `GITHUB_SETUP.md` | 5 min |
| **Build Wait** | GitHub Actions runs automatically | 5-10 min |
| **Download APK** | Actions → Artifacts or Releases | 1 min |
| **Install** | Transfer to phone and install | 2 min |

---

## 📁 Important Files

| File | Purpose |
|------|---------|
| `START_HERE.md` | This file - start here! |
| `GITHUB_SETUP.md` | Detailed GitHub instructions |
| `QUICK_START.md` | Quick reference for using the app |
| `USER_GUIDE.md` | Complete user manual |
| `BANK_SMS_PATTERNS.md` | Customize SMS detection |

---

## ⚠️ Common Issues

### Issue: Gradle wrapper download fails

**Solution**: Download manually from the link above and place in `gradle/wrapper/`

### Issue: Git push fails

**Solution**: Use Personal Access Token instead of password (see GITHUB_SETUP.md)

### Issue: Build fails on GitHub

**Solution**:
- Check that `gradle-wrapper.jar` was uploaded
- Check Actions logs for specific error
- Ensure all files were pushed (`git status`)

### Issue: Can't install APK on phone

**Solution**: Enable "Install from Unknown Sources" in Security settings

---

## 🎉 You're Almost There!

1. ✅ Run `setup-gradle-wrapper.bat`
2. ✅ Open `GITHUB_SETUP.md` and follow it
3. ✅ Wait for GitHub to build your APK
4. ✅ Download and install on phone
5. ✅ Enjoy automatic expense tracking!

---

## 🆘 Need Help?

- **GitHub Setup**: See `GITHUB_SETUP.md`
- **Using the App**: See `USER_GUIDE.md`
- **SMS Detection**: See `BANK_SMS_PATTERNS.md`
- **Quick Tips**: See `QUICK_START.md`

---

## 📱 After Installation

Your app will:
- ✅ Automatically detect bank SMS
- ✅ Track expenses by category
- ✅ Show remaining budget
- ✅ Send daily reminders at 8 AM
- ✅ Alert you at 80% and 100% budget
- ✅ Help you save money!

---

**Ready? Start with Step 1 above!**

Good luck with your expense tracking journey! 💰📊
