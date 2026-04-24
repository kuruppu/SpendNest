# Bank SMS Pattern Configuration Guide

This document explains how to customize the SMS parser to work with your bank's SMS format.

## How SMS Detection Works

The app uses regex patterns to detect and parse bank transaction SMS. The parser is located in:
```
app/src/main/java/com/expense/tracker/utils/SmsParser.kt
```

## Current Supported Patterns

### Card Payments

The app currently recognizes these patterns:

1. **Pattern 1**: `Rs.1,234.56 spent on Card ending 5678`
2. **Pattern 2**: `Card txn: Rs 1234.56`
3. **Pattern 3**: `Purchase of Rs.1234.56`
4. **Pattern 4**: `Debit Card ending 5678 for Rs.1234.56`

### ATM Withdrawals

1. **Pattern 1**: `ATM withdrawal of Rs.5000`
2. **Pattern 2**: `Cash withdrawn Rs.5000`
3. **Pattern 3**: `Withdrawn Rs.5000 from ATM`

### Available Balance

The parser also extracts available balance if present:
- `Available balance: Rs.45,320.50`
- `Avbl Bal: Rs.45320.50`
- `Bal: Rs 45320`

## Adding Support for Your Bank

### Step 1: Identify Your Bank's SMS Format

Save some actual transaction SMS from your bank. Examples:

**Bank A:**
```
Your A/C XX1234 debited with INR 2,500.00 on 15-Jan-25.
Available Balance: INR 45,320.50
```

**Bank B:**
```
Rs 1250 debited from card **5678 at MERCHANT on 15/01/25
```

### Step 2: Add New Regex Pattern

Open `SmsParser.kt` and add your pattern to the appropriate list:

#### For Card Payments:

```kotlin
private val cardPaymentPatterns = listOf(
    // Existing patterns...

    // Add your bank's pattern here:
    Regex("""debited.*?(?:Rs\.?|INR|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE),
)
```

#### For ATM Withdrawals:

```kotlin
private val atmWithdrawalPatterns = listOf(
    // Existing patterns...

    // Add your bank's pattern here:
    Regex("""withdrawn.*?(?:Rs\.?|INR|LKR)\s*([\d,]+\.?\d*).*?ATM""", RegexOption.IGNORE_CASE),
)
```

### Step 3: Test Your Pattern

1. Create a test SMS with your bank's format
2. Run the app
3. Check logcat for parsing results:
   ```
   adb logcat | grep SmsReceiver
   ```

## Regex Pattern Explanation

### Basic Components

- `(?:Rs\.?|INR|LKR)` - Matches currency symbols (Rs, Rs., INR, LKR)
- `\s*` - Matches zero or more whitespace
- `([\d,]+\.?\d*)` - Captures the amount (with commas and decimal)
- `.*?` - Matches any characters (non-greedy)
- `RegexOption.IGNORE_CASE` - Case-insensitive matching

### Example Breakdown

Pattern: `Card txn: Rs 1234.56`

```kotlin
Regex("""card.*?(?:Rs\.?|LKR)\s*([\d,]+\.?\d*)""", RegexOption.IGNORE_CASE)
```

- `card` - Matches "card" (case-insensitive)
- `.*?` - Matches any characters between "card" and currency
- `(?:Rs\.?|LKR)` - Matches "Rs", "Rs." or "LKR"
- `\s*` - Matches whitespace
- `([\d,]+\.?\d*)` - Captures "1234.56"

## Common Bank SMS Formats

### Sri Lankan Banks

**Bank of Ceylon:**
```
Dear Customer, your Card ending 1234 was used for LKR 2,500.00 at MERCHANT.
Available Balance: LKR 25,000.00
```

**Commercial Bank:**
```
Your Card **5678 used for Rs. 1,250.00 on 15/01/25.
Avbl Bal: Rs. 45,320.50
```

**Sampath Bank:**
```
Rs.2500.00 debited from Card ending 5678 on 15-Jan-25
Available Balance Rs.45320.50
```

### Indian Banks

**HDFC Bank:**
```
Your A/C XX1234 debited with INR 2,500.00 on 15-Jan-25.
Available Balance: INR 45,320.50
```

**ICICI Bank:**
```
Your Card ending 5678 has been used for INR 1,250.00 at MERCHANT on 15/01/25
```

**SBI:**
```
Rs 2500.00 debited from A/C XX1234 on 15Jan25.
Avl Bal: Rs 45320.50
```

## Testing Without Real Transactions

### Method 1: Emulator SMS

1. Open Android Emulator
2. Click the **...** (More) button
3. Go to **Phone > Messages**
4. Send a test SMS with your bank's format

### Method 2: ADB Command

```bash
adb emu sms send +1234567890 "Card txn: Rs 1250.00 spent on Card ending 5678"
```

### Method 3: Test App

Install an SMS testing app from Play Store to send yourself test messages.

## Debugging Tips

### Enable Detailed Logging

Add this to `SmsParser.kt`:

```kotlin
fun parseTransactionSms(message: String): ParsedTransaction? {
    Log.d("SmsParser", "Parsing message: $message")

    // Try each pattern and log results
    cardPaymentPatterns.forEachIndexed { index, pattern ->
        pattern.find(message)?.let { match ->
            Log.d("SmsParser", "Matched card pattern $index: ${match.value}")
            // ... rest of code
        }
    }
    // ... rest of code
}
```

### Check Logcat

```bash
# View all SMS receiver logs
adb logcat | grep SmsReceiver

# View parsing logs
adb logcat | grep SmsParser
```

### Common Issues

**Issue**: Pattern not matching

**Solutions**:
- Check for extra spaces or special characters
- Use `.*?` for flexible matching
- Test regex at https://regex101.com/

**Issue**: Amount not extracted correctly

**Solutions**:
- Verify capture group: `([\d,]+\.?\d*)`
- Check for currency format differences
- Handle both comma and decimal formats

## Contributing Patterns

If you add support for a new bank, please document it here:

### Your Bank Name
```
SMS Format: [paste example SMS]
Pattern Added: [paste your regex]
Date: [date added]
```

This helps other users with the same bank!

## Security Note

The app only reads SMS from known senders (banks) and only processes transaction-related messages. All data stays local on your device.

## Need Help?

If you're having trouble creating a pattern for your bank:

1. Save 3-4 example SMS from different transactions
2. Note any variations in format
3. Try the basic patterns first
4. Use regex testing tools
5. Check logcat for detailed parsing info
