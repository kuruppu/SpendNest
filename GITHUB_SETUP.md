# GitHub Actions Setup - Get Your APK

This guide will help you upload the project to GitHub and automatically build the APK.

## What You Need

1. **GitHub Account** (free)
   - Sign up at: https://github.com/signup

2. **Git Installed** on your computer
   - Download: https://git-scm.com/downloads
   - Install with default settings

## Step-by-Step Instructions

### Step 1: Install Git (if not installed)

1. Download Git from: https://git-scm.com/downloads
2. Run installer
3. Use default settings (just click Next)
4. Finish installation

**Verify installation:**
```bash
# Open Command Prompt and run:
git --version
```
You should see: `git version 2.x.x`

### Step 2: Create GitHub Account

1. Go to: https://github.com/signup
2. Enter your email
3. Create password
4. Choose username
5. Verify email

### Step 3: Create New Repository

1. Log in to GitHub
2. Click the **+** icon (top-right) → **New repository**
3. Fill in details:
   - **Repository name**: `expense-tracker-app`
   - **Description**: `Android expense tracking app with SMS auto-detection`
   - **Visibility**: Public or Private (your choice)
   - **DO NOT** initialize with README (we already have files)
4. Click **Create repository**

### Step 4: Push Project to GitHub

Open Command Prompt or PowerShell and navigate to project:

```bash
# Navigate to project directory
cd D:\Projects\ExpenseTracker

# Initialize git repository
git init

# Add all files
git add .

# Create first commit
git commit -m "Initial commit: Expense Tracker Android app"

# Add GitHub as remote
# Replace YOUR-USERNAME with your actual GitHub username
git remote add origin https://github.com/YOUR-USERNAME/expense-tracker-app.git

# Set main branch
git branch -M main

# Push to GitHub
git push -u origin main
```

**Important**: Replace `YOUR-USERNAME` with your actual GitHub username.

**If asked for credentials:**
- Username: Your GitHub username
- Password: Use a Personal Access Token (not your password)

### Step 5: Create Personal Access Token (if needed)

If push fails asking for password:

1. Go to: https://github.com/settings/tokens
2. Click **Generate new token** → **Generate new token (classic)**
3. Give it a name: `ExpenseTracker`
4. Select scopes:
   - ✓ `repo` (all)
   - ✓ `workflow`
5. Click **Generate token**
6. **COPY THE TOKEN** (you won't see it again!)
7. Use this token as password when pushing

### Step 6: Wait for Build

1. Go to your GitHub repository
2. Click **Actions** tab
3. You'll see "Build Android APK" running
4. Wait 5-10 minutes for build to complete
5. Green checkmark = Success!

### Step 7: Download APK

**Option A: From Actions (Every Build)**
1. Go to **Actions** tab
2. Click the latest successful workflow
3. Scroll down to **Artifacts**
4. Click **expense-tracker-debug** to download
5. Unzip the downloaded file
6. Transfer `app-debug.apk` to your phone

**Option B: From Releases (Automatic)**
1. Go to **Releases** (right sidebar)
2. Click the latest release
3. Download `app-debug.apk` directly
4. Transfer to your phone

### Step 8: Install on Phone

1. Transfer `app-debug.apk` to your phone via:
   - USB cable
   - Email to yourself
   - Google Drive / Dropbox
   - Bluetooth

2. On your phone:
   - Open the APK file
   - Tap **Install**
   - If blocked, enable **"Install from Unknown Sources"**:
     - Settings → Security → Unknown Sources → Enable

3. Open the app and enjoy!

## Updating the App

When you make changes:

```bash
# Navigate to project
cd D:\Projects\ExpenseTracker

# Add changes
git add .

# Commit changes
git commit -m "Description of changes"

# Push to GitHub
git push
```

GitHub Actions will automatically build a new APK!

## Quick Commands Reference

```bash
# Check status
git status

# Add all changes
git add .

# Commit
git commit -m "Your message here"

# Push to GitHub
git push

# Pull latest from GitHub
git pull
```

## Troubleshooting

### Problem: Git not recognized

**Solution:**
- Restart Command Prompt after installing Git
- Or add Git to PATH manually

### Problem: Authentication failed

**Solution:**
- Use Personal Access Token instead of password
- Follow Step 5 above

### Problem: Build failed on GitHub

**Solution:**
1. Go to Actions tab
2. Click failed workflow
3. Check error messages
4. Common issues:
   - Missing files (check .gitignore)
   - Syntax errors in code

### Problem: Can't install APK on phone

**Solution:**
1. Enable Unknown Sources:
   - Settings → Security → Unknown Sources
2. Or Settings → Apps → Special Access → Install Unknown Apps
3. Enable for your file manager

### Problem: APK not in Releases

**Solution:**
- Releases are created only on main/master branch
- Check Actions tab for Artifacts instead
- Or wait for next push to main branch

## File Locations

After download:
- **Windows**: `C:\Users\YourName\Downloads\expense-tracker-debug.zip`
- **Extract**: Right-click → Extract All
- **APK**: Inside extracted folder: `app-debug.apk`

## Viewing Build Logs

1. Go to **Actions** tab
2. Click workflow run
3. Click **build** job
4. Expand steps to see detailed logs
5. Useful for debugging build errors

## GitHub Actions Features

Your workflow will:
- ✅ Automatically build APK on every push
- ✅ Create downloadable artifacts
- ✅ Create releases with version numbers
- ✅ Run on free GitHub runners
- ✅ No cost for public repositories

## Build Time

- First build: ~10 minutes (downloads dependencies)
- Subsequent builds: ~5 minutes (uses cache)

## Sharing Your App

**Share the GitHub Release URL:**
```
https://github.com/YOUR-USERNAME/expense-tracker-app/releases
```

Others can download the APK from there!

## Next Steps

1. ✅ Push code to GitHub
2. ✅ Wait for build to complete
3. ✅ Download APK
4. ✅ Install on phone
5. ✅ Start tracking expenses!

## Support

If you encounter issues:
- Check GitHub Actions logs
- Ensure all files were pushed (`git status`)
- Verify workflow file exists: `.github/workflows/build-apk.yml`

---

**Summary Commands:**

```bash
cd D:\Projects\ExpenseTracker
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR-USERNAME/expense-tracker-app.git
git branch -M main
git push -u origin main
```

Then visit: https://github.com/YOUR-USERNAME/expense-tracker-app/actions

Download APK from Actions → Artifacts or Releases!
