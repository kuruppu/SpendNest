#!/bin/bash

echo "========================================"
echo "Gradle Wrapper Setup"
echo "========================================"
echo ""
echo "This script will download the Gradle Wrapper JAR"
echo "Required for GitHub Actions to build your APK"
echo ""

mkdir -p gradle/wrapper

echo "Downloading gradle-wrapper.jar..."
curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar

if [ -f "gradle/wrapper/gradle-wrapper.jar" ]; then
    echo ""
    echo "✓ Success! Gradle wrapper is ready."
    echo ""
    echo "Next steps:"
    echo "1. Follow GITHUB_SETUP.md to push to GitHub"
    echo "2. GitHub Actions will automatically build your APK"
    echo ""
else
    echo ""
    echo "✗ Download failed. Please check your internet connection."
    echo ""
    echo "Manual download:"
    echo "1. Visit: https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
    echo "2. Save to: gradle/wrapper/gradle-wrapper.jar"
    echo ""
fi
