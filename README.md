# Expense Tracker Android App

A minimal, clean Android expense tracking app built with Kotlin and Jetpack Compose. Automatically tracks expenses from bank SMS and manages category-based budgets with clear visibility of remaining money and spending behavior.

## Features

### Core Functionality

1. **Custom Billing Cycle**
   - Select your own cycle start date (1st-31st of month)
   - All budgets, reports, and calculations follow your custom cycle
   - Example: If you select 25th, cycle runs from 25th of current month to 25th of next month

2. **SMS Transaction Detection**
   - Automatically detects bank SMS for card payments and ATM withdrawals
   - Extracts amount, date/time, and available balance
   - Flexible regex parsing supports different bank formats

3. **Auto Expense Handling**
   - Card payments: Automatically logged as expenses
   - Cash withdrawals: Initially logged as "Cash Expense"
   - Sends notification after each transaction: "What was this expense for?"
   - Reminder notifications for cash withdrawals to categorize actual usage

4. **Category & Subcategory System**
   - Main Categories: Food, Transport, Kids, Bills, Savings, Other, Cash Expense
   - Subcategories:
     - Savings: Kids Saving, Own Saving
     - Bills: Credit Card, Electricity, Mobile, Internet

5. **Budget Management**
   - Set budget per category per cycle
   - Automatic budget deduction when expenses are added
   - Real-time remaining amount tracking
   - Budget alerts at 80% usage and when exceeded

6. **Daily Reminders**
   - Daily notification at 8:00 AM
   - Shows remaining budget for current cycle
   - Configurable in settings

7. **Clean, Minimal UI**
   - Material Design 3 with Jetpack Compose
   - Soft color palette
   - Easy navigation
   - Progress indicators for budget tracking
   - Optional pie charts for spending visualization

## Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with Repository Pattern
- **Database**: Room Database
- **Background Tasks**: WorkManager
- **Minimum SDK**: API 26 (Android 8.0)
- **Target SDK**: API 34 (Android 14)

## Project Structure

```
app/src/main/java/com/expense/tracker/
├── data/
│   ├── dao/                    # Room DAOs
│   ├── database/               # Database and converters
│   ├── entity/                 # Data entities
│   └── repository/             # Repository layer
├── domain/
│   └── model/                  # Domain models
├── receiver/
│   └── SmsReceiver.kt         # SMS broadcast receiver
├── ui/
│   ├── components/            # Reusable UI components
│   ├── navigation/            # Navigation setup
│   ├── screens/               # App screens
│   └── theme/                 # Theme configuration
├── utils/                     # Utility classes
├── worker/                    # WorkManager workers
├── ExpenseTrackerApp.kt      # Application class
└── MainActivity.kt            # Main activity
```

## Screens

1. **Dashboard**
   - Summary of total budget, spent, and remaining
   - Category breakdown with progress indicators
   - Quick access to all features

2. **Category Detail**
   - Detailed view of spending in a category
   - Subcategory breakdown (if enabled)
   - Transaction history for the category

3. **Transaction History**
   - Complete list of all transactions
   - Shows date, amount, and category
   - Ability to edit categories

4. **Budget Setup**
   - Configure budgets for each category
   - Easy-to-use input fields
   - Saves and updates budgets

5. **Settings**
   - Billing cycle start day selection
   - Daily reminder toggle
   - Display preferences (pie charts, subcategories)
   - Permission management

6. **Add Transaction**
   - Manually add transactions
   - Select category and enter amount
   - Optional description field

## Setup Instructions

### Prerequisites
- Android Studio (latest version)
- JDK 17
- Android SDK 34

### Building the Project

1. Clone or download the project to `D:\Projects\ExpenseTracker`

2. Open Android Studio and select "Open an existing project"

3. Navigate to `D:\Projects\ExpenseTracker` and open it

4. Wait for Gradle sync to complete

5. Build the project: **Build > Make Project**

6. Run on emulator or device: **Run > Run 'app'**

### Permissions Required

The app requires the following permissions:
- `RECEIVE_SMS`: To detect incoming bank SMS
- `READ_SMS`: To read SMS content
- `POST_NOTIFICATIONS`: For notifications (Android 13+)
- `SCHEDULE_EXACT_ALARM`: For daily reminders

These permissions are requested at runtime when the app starts.

## Usage Guide

### First Time Setup

1. Launch the app
2. Grant SMS and notification permissions when prompted
3. Navigate to Settings and set your billing cycle start day
4. Go to Dashboard and tap "Set Up Your Budgets"
5. Enter budget amounts for each category
6. You're all set!

### Adding Transactions

**Automatic (via SMS):**
- When you make a card payment or ATM withdrawal, the app automatically detects the bank SMS
- You'll receive a notification to categorize the expense
- Tap the notification to select the appropriate category

**Manual:**
- Tap the "+" button on the Dashboard
- Enter amount, select category, and add optional description
- Tap "Add Transaction"

### Managing Budgets

- Go to Dashboard > Tap "Budget Setup"
- Update budget amounts for any category
- Changes take effect immediately

### Customizing Settings

- Access Settings from the Dashboard
- Configure:
  - Billing cycle start day
  - Daily reminder on/off
  - Show/hide pie charts
  - Show/hide subcategory breakdown

## Features in Detail

### SMS Parsing

The app uses regex patterns to detect and parse bank SMS:
- Supports multiple bank formats
- Extracts amount, transaction type, and available balance
- Differentiates between card payments and ATM withdrawals

### Budget Alerts

- **80% Alert**: "You are nearing your budget limit"
- **Exceeded Alert**: "Budget exceeded. Reduce spending."
- Alerts are shown on the Dashboard and via notifications

### Cash Expense Tracking

- Cash withdrawals are initially categorized as "Cash Expense"
- Reminders are sent at 4, 12, and 24 hours to categorize actual spending
- Helps track where cash is actually spent

## Future Enhancements

- Pie chart visualization for category spending
- Export transactions to CSV/Excel
- Monthly/yearly spending reports
- Budget recommendations based on spending patterns
- Multi-currency support
- Backup and restore functionality

## Troubleshooting

**SMS not detected:**
- Check if SMS permissions are granted
- Verify the bank SMS format is supported
- Check logcat for parsing errors

**Notifications not working:**
- Ensure notification permission is granted
- Check battery optimization settings
- Verify notification channels are not muted

**Daily reminder not showing:**
- Enable daily reminders in Settings
- Check if app has permission to schedule exact alarms
- Verify battery optimization is disabled for the app

## License

This project is provided as-is for educational and personal use.

## Support

For issues or questions, please refer to the inline code documentation.
