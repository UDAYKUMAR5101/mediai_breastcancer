  # MEDIAI APP - Fragment-Based Navigation

A modern Android app with fragment-based navigation using Android Jetpack Navigation Component.

## 🎯 Features

- **Fragment-Based Architecture**: Uses Android Navigation Component for smooth navigation
- **Bottom Navigation**: 4 main sections with modern Material Design
- **Dashboard**: Health monitoring dashboard with cards and recent activity
- **Risk Assessment**: Risk level evaluation with detailed analysis
- **AI Chat Bot**: Interactive chat interface for health queries
- **Settings**: Comprehensive settings with profile, notifications, and account management

## 📱 App Structure

### Main Components

1. **MainActivity**: Hosts the NavHostFragment and BottomNavigationView
2. **DashboardFragment**: Home screen with health monitoring cards
3. **RiskFragment**: Risk assessment and analysis screen
4. **ChatFragment**: AI chat interface
5. **SettingsFragment**: App settings and account management

### Navigation

- **nav_home** → DashboardFragment
- **nav_risk** → RiskFragment  
- **nav_chat** → ChatFragment
- **nav_more** → SettingsFragment

### Bottom Navigation

- **Home**: Dashboard with health monitoring
- **Risk Level**: Risk assessment tools
- **AI Chat Bot**: Interactive health assistant
- **More**: Settings and account management

## 🎨 Design Features

- **Material Design 3**: Modern UI components
- **Bottom Navigation**: Fixed at bottom with 4 items
- **Color Scheme**: 
  - Active: #2F80ED (Bright Blue)
  - Inactive: #BDBDBD (Light Gray)
- **Touch Targets**: All interactive elements meet 48dp minimum
- **Elevation**: Subtle shadows and elevation for depth

## 🛠 Technical Stack

- **Kotlin**: Primary language
- **Android Jetpack Navigation**: Fragment navigation
- **Material Components**: UI components
- **ConstraintLayout**: Layout system
- **RecyclerView**: For chat messages
- **CardView**: For dashboard cards

## 📁 Project Structure

```
app/src/main/
├── java/com/simats/mediai_app/
│   ├── MainActivity.kt
│   ├── DashboardFragment.kt
│   ├── RiskFragment.kt
│   ├── ChatFragment.kt
│   └── SettingsFragment.kt
├── res/
│   ├── layout/
│   │   ├── activity_main.xml
│   │   ├── fragment_dashboard.xml
│   │   ├── fragment_risk.xml
│   │   ├── fragment_chat.xml
│   │   └── fragment_settings.xml
│   ├── menu/
│   │   └── bottom_nav_menu.xml
│   ├── navigation/
│   │   └── nav_graph.xml
│   └── drawable/
│       ├── ic_home.xml
│       ├── ic_risk.xml
│       ├── ic_chat.xml
│       └── ic_settings.xml
```

## 🚀 Getting Started

1. Open the project in Android Studio
2. Sync Gradle files
3. Build and run the app
4. The app will launch to the Dashboard (Home) fragment

## 📋 Dependencies

- `androidx.navigation:fragment-ktx`
- `androidx.navigation:ui-ktx`
- `com.google.android.material:material`
- `androidx.fragment:fragment-ktx`

## 🎯 Key Features

### Dashboard
- Health monitoring cards
- Recent activity feed
- Quick access to main features

### Risk Assessment
- Risk level visualization
- Detailed analysis
- Recommended actions

### AI Chat
- Interactive chat interface
- Message input with send button
- Scrollable message area

### Settings
- Profile management
- Notification preferences
- Privacy & security settings
- Account management

## 🔧 Customization

The app is designed to be easily customizable:
- Colors can be modified in `themes.xml`
- Navigation items can be added in `bottom_nav_menu.xml`
- Fragment layouts can be customized in their respective XML files
- Navigation graph can be extended in `nav_graph.xml`

## 📱 Screenshots

The app features a clean, modern interface with:
- White background with subtle elevation
- Blue accent color (#2F80ED)
- Card-based layouts
- Material Design components
- Bottom navigation similar to popular apps like Wolt

## 🎉 Ready to Use

The app is fully functional and ready to build and run in Android Studio. All navigation, layouts, and basic functionality are implemented. 