# Expense Tracker - User Guide

Complete guide to using all features of the Expense Tracker app.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Dashboard](#dashboard)
3. [Adding Transactions](#adding-transactions)
4. [Managing Budgets](#managing-budgets)
5. [Viewing Transaction History](#viewing-transaction-history)
6. [Category Details](#category-details)
7. [Settings](#settings)
8. [Notifications](#notifications)
9. [Tips & Best Practices](#tips--best-practices)

---

## Getting Started

### First Launch

When you first open the app:

1. **Grant Permissions**
   - SMS Read/Receive: For automatic transaction detection
   - Notifications: For alerts and reminders
   - Tap "Allow" when prompted

2. **Set Billing Cycle**
   - Tap the Settings icon (⚙️)
   - Tap "Cycle Start Day"
   - Enter the day your billing cycle starts (1-31)
   - Example: If your salary comes on the 25th, set it to 25

3. **Configure Budgets**
   - Return to Dashboard
   - Tap "Set Up Your Budgets"
   - Enter monthly budget for each category
   - Tap "Save Budgets"

You're now ready to track expenses!

---

## Dashboard

The Dashboard is your main screen showing:

### Summary Card

Shows your current billing cycle:
- **Total Budget**: Sum of all category budgets
- **Spent**: Total amount spent this cycle
- **Remaining**: Budget - Spent (in large text)
- Cycle dates (e.g., "Dec 25 - Jan 25")

Colors:
- Green: You're within budget
- Red: You've exceeded budget

### Category Cards

Each category shows:
- **Name**: Category name
- **Progress Bar**: Visual representation of spending
- **Percentage**: % of budget used
- **Spent**: Amount spent in this category
- **Budget**: Total budget for category
- **Remaining**: Budget - Spent

Warning Messages:
- "You are nearing your budget limit" (at 80%)
- "Budget exceeded. Reduce spending." (over 100%)

### Actions

- **+ Button** (bottom-right): Add manual transaction
- **History Icon**: View all transactions
- **Settings Icon**: Configure app settings

---

## Adding Transactions

### Automatic (via SMS)

**How it works:**
1. You make a card payment or ATM withdrawal
2. Your bank sends you an SMS
3. App automatically detects and logs the transaction
4. You receive a notification: "What was this expense for?"
5. Tap notification to categorize

**Card Payments:**
- Automatically logged as expenses
- Initially assigned to "Other" category
- Notification prompts you to assign correct category

**Cash Withdrawals:**
- Initially logged as "Cash Expense"
- You'll receive reminders to categorize actual spending
- Reminders at: 4 hours, 12 hours, 24 hours

### Manual

**To add a transaction manually:**

1. Tap the **+ button** on Dashboard

2. Enter details:
   - **Amount**: Transaction amount in LKR
   - **Category**: Select from dropdown
   - **Description**: Optional notes

3. Tap "Add Transaction"

**When to use manual entry:**
- Cash purchases (not from ATM)
- Online payments not via card
- Subscription payments
- Any expense not detected automatically

---

## Managing Budgets

### Setting Initial Budgets

1. Dashboard → "Set Up Your Budgets"
2. Enter amount for each category
3. Tap "Save Budgets"

### Updating Budgets

1. Dashboard → Settings → Budget Setup
2. Modify amounts
3. Tap "Save Budgets"

Changes take effect immediately for the current cycle.

### Budget Tips

**Food**: Groceries + Dining out
- Example: LKR 30,000/month

**Transport**: Fuel + Public transport + Maintenance
- Example: LKR 15,000/month

**Kids**: School fees + Activities + Supplies
- Example: LKR 20,000/month

**Bills**: Utilities + Subscriptions + Credit cards
- Example: LKR 25,000/month
- Use subcategories for detailed tracking

**Savings**: Money set aside
- Example: LKR 40,000/month
- Subcategories: Kids Saving, Own Saving

**Other**: Everything else
- Example: LKR 10,000/month

**Cash Expense**: Temporary category
- No budget needed (transactions moved to other categories)

---

## Viewing Transaction History

### All Transactions

1. Dashboard → History Icon
2. View complete list of transactions
3. Sorted by date (newest first)

**Information shown:**
- Date and time
- Amount
- Category
- Description (if added)
- Categorization status

### Filter by Category

1. Dashboard → Tap any category card
2. View transactions for that category only
3. Limited to current billing cycle

---

## Category Details

### Accessing Category Details

Tap any category card on Dashboard

### What You'll See

1. **Subcategory Breakdown** (if enabled)
   - Shows spending by subcategory
   - Percentage and amount for each
   - Progress bars for visual comparison

2. **Transaction List**
   - All transactions for this category
   - Current billing cycle only
   - Date, time, and amount

**Example - Bills Category:**
```
Subcategory Breakdown:
- Credit Card: 45% (LKR 11,250)
- Electricity: 30% (LKR 7,500)
- Mobile: 15% (LKR 3,750)
- Internet: 10% (LKR 2,500)
```

---

## Settings

Access via Settings icon on Dashboard.

### Billing Cycle

**Cycle Start Day**
- Set the day your cycle begins (1-31)
- Example: Salary day = Cycle start
- Changes affect next cycle calculation

**How it works:**
- If start day is 25th:
  - Current cycle: Dec 25 - Jan 25
  - Next cycle: Jan 25 - Feb 25

### Notifications

**Daily Reminder**
- Toggle on/off
- Sent at 8:00 AM every day
- Shows remaining budget
- Shows cycle dates

**Budget Alerts**
- Cannot be disabled
- Sent at 80% and 100% of budget
- Helps you stay on track

### Display Preferences

**Show Pie Charts**
- Enable/disable pie charts (future feature)
- Currently not implemented

**Show Subcategory Breakdown**
- Show/hide subcategory details
- Applies to Category Detail screen
- Useful if you don't use subcategories

### Permissions

**SMS Permissions**
- Tap to request/verify permissions
- Required for auto-detection
- Safe: Only reads bank SMS

---

## Notifications

### Transaction Detected

**Appears when:**
- Bank SMS is received
- Transaction is automatically logged

**Content:**
- "Card Payment Detected" or "Cash Withdrawal Detected"
- Amount (LKR X.XX)
- "What was this expense for?"

**Action:**
- Tap to open app and categorize

### Daily Reminder

**Appears at:**
- 8:00 AM every day

**Content:**
- "Daily Budget Reminder"
- "You have LKR X remaining this cycle"
- Current cycle dates

**Purpose:**
- Keep you aware of spending
- Encourage budget consciousness

### Budget Alert (80%)

**Appears when:**
- Category spending reaches 80% of budget

**Content:**
- "Budget Alert"
- "[Category] budget at 80%"
- "You are nearing your budget limit"

**Action:**
- Review spending in that category
- Consider reducing expenses

### Budget Exceeded (100%)

**Appears when:**
- Category spending exceeds budget

**Content:**
- "Budget Exceeded!"
- "[Category] budget exceeded"
- "Reduce spending"

**Action:**
- Stop spending in that category if possible
- Review if budget needs adjustment

### Cash Expense Reminder

**Appears:**
- 4, 12, and 24 hours after ATM withdrawal

**Content:**
- "Cash Expense Reminder"
- "You withdrew LKR X"
- "Categorize your spending"

**Purpose:**
- Remind you to track where cash was spent
- Move from "Cash Expense" to actual category

---

## Tips & Best Practices

### 1. Set Realistic Budgets

- Review past spending before setting budgets
- Start conservatively
- Adjust based on actual spending patterns

### 2. Categorize Immediately

- Categorize transactions as soon as notification arrives
- Don't let them pile up
- More accurate tracking when fresh in memory

### 3. Use Subcategories

- Bills: Break down by utility type
- Savings: Separate kids vs personal savings
- Helps identify specific spending patterns

### 4. Review Weekly

- Check Dashboard every week
- See which categories are over/under budget
- Adjust spending accordingly

### 5. Track Cash Properly

- When you withdraw cash, note what it's for
- Categorize cash expenses within 24 hours
- Prevents "Cash Expense" category from growing

### 6. Adjust Billing Cycle

- Set cycle start to match income schedule
- Salary on 25th? Set cycle to 25th
- Easier to match income with expenses

### 7. Use Daily Reminders

- Keep daily reminder enabled
- Quick check of budget status
- Builds spending awareness

### 8. Plan for Irregular Expenses

- Annual insurance: Add to monthly Bills budget
- Quarterly payments: Include in monthly calculation
- Example: LKR 12,000/year = LKR 1,000/month

### 9. Emergency Buffer

- Set "Other" category as emergency buffer
- Small amount for unexpected expenses
- Prevents exceeding other category budgets

### 10. Review and Adjust

- Review budgets at end of each cycle
- Increase budgets that consistently exceed
- Decrease budgets with large remainders
- Aim for realistic, sustainable budgets

---

## Common Scenarios

### Scenario 1: Large Unexpected Expense

**Problem**: Medical bill of LKR 15,000, no budget

**Solution**:
1. Add transaction manually
2. Assign to "Other" category
3. Next cycle, adjust "Other" budget up
4. Or create new "Medical" category

### Scenario 2: Shared Expenses

**Problem**: Paid for group dinner, will be reimbursed

**Solution**:
1. Let transaction auto-log
2. Assign to "Food"
3. When reimbursed, don't log that as income
4. Consider adding description: "Reimbursed"

### Scenario 3: Multiple Small Cash Purchases

**Problem**: Withdrew LKR 5,000, spent on multiple things

**Solution**:
1. Initial withdrawal logs as "Cash Expense"
2. Add separate manual transactions:
   - LKR 2,000 → Food (Groceries)
   - LKR 1,500 → Transport (Fuel)
   - LKR 1,000 → Other (Miscellaneous)
   - LKR 500 → Food (Snacks)
3. Delete or ignore the "Cash Expense" entry

### Scenario 4: Budget Always Exceeded

**Problem**: "Food" budget always 120-150%

**Solution**:
1. Review transactions: Are they all necessary?
2. If yes, increase Food budget
3. Decrease another category to compensate
4. Maintain total budget balance

### Scenario 5: Forgot to Categorize

**Problem**: 5 transactions showing "Needs categorization"

**Solution**:
1. Go to Transaction History
2. Check SMS messages for context
3. Tap each transaction
4. Assign correct categories
5. Enable daily reminders to avoid this

---

## Troubleshooting

### SMS Not Detected

**Check:**
- SMS permissions granted?
- Is it a bank SMS?
- Does pattern match your bank's format?

**Solution:**
- Grant permissions in Settings
- Add your bank's pattern (see BANK_SMS_PATTERNS.md)
- Add transaction manually as workaround

### Wrong Amount Detected

**Cause:**
- SMS contains multiple amounts
- Available balance parsed as transaction

**Solution:**
- Check transaction in History
- Edit amount if needed
- Report pattern issue

### Notifications Not Showing

**Check:**
- Notification permission granted?
- App notifications enabled in phone settings?
- Do Not Disturb mode off?

**Solution:**
- Settings → Permissions → Enable notifications
- Check phone's notification settings
- Disable battery optimization for app

---

## Keyboard Shortcuts (Future)

Coming soon: Quick actions for power users

---

## FAQ

**Q: Can I have multiple billing cycles?**
A: No, currently one cycle for all categories.

**Q: Can I export my data?**
A: Not yet, planned for future update.

**Q: How do I delete a transaction?**
A: Long-press on transaction (coming soon).

**Q: Can I set daily/weekly budgets?**
A: No, currently monthly budgets only.

**Q: Does this work with multiple currencies?**
A: Currently LKR only, multi-currency planned.

**Q: Is my data backed up?**
A: Data is local only. Backup feature planned.

---

## Getting Help

- Review this guide
- Check SETUP.md for technical issues
- Check BANK_SMS_PATTERNS.md for SMS issues
- Check app logs in Android Studio

---

Happy tracking! Manage your expenses wisely.
