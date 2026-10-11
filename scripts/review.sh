#!/usr/bin/env bash
set -e

echo "========================================="
echo "    SVA Master Automated Code Reviewer   "
echo "========================================="

FAILURES=0

# 1. Check for banned bloated dependencies
echo ""
echo "[1/4] Checking for banned dependencies..."
if grep -rn "material-icons-extended" app/build.gradle.kts gradle/libs.versions.toml 2>/dev/null; then
    echo "❌ ERROR: Found banned dependency 'material-icons-extended' (bloats APK and slows builds)!"
    FAILURES=$((FAILURES + 1))
else
    echo "✅ No banned dependencies found."
fi

# 2. Check for offline invariant (no internet permission)
echo ""
echo "[2/4] Checking privacy & offline-first invariant..."
if grep -rn "android.permission.INTERNET" app/ 2>/dev/null; then
    echo "❌ ERROR: Found 'android.permission.INTERNET'! SVA Master must remain 100% offline."
    FAILURES=$((FAILURES + 1))
else
    echo "✅ 100% offline guarantee preserved (no INTERNET permission)."
fi

# 3. Run automated unit & data integrity tests
echo ""
echo "[3/4] Running automated unit & data integrity tests..."
if ./gradlew test --quiet; then
    echo "✅ All unit and data integrity tests passed."
else
    echo "❌ ERROR: Unit tests failed!"
    FAILURES=$((FAILURES + 1))
fi

# 4. Run compilation check and APK assembly
echo ""
echo "[4/4] Running build verification..."
if ./gradlew assembleDebug --quiet; then
    echo "✅ Build and debug APK verification passed."
else
    echo "❌ ERROR: Build failed!"
    FAILURES=$((FAILURES + 1))
fi

echo ""
echo "========================================="
if [ $FAILURES -eq 0 ]; then
    echo "   🎉 CODE REVIEW PASSED: All Checks OK!  "
    echo "========================================="
    exit 0
else
    echo "   ❌ CODE REVIEW FAILED: $FAILURES issue(s) found. "
    echo "========================================="
    exit 1
fi
