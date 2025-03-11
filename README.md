# TrojanHorse App Interceptor

A proof-of-concept Android application demonstrating overlay capabilities and application monitoring.

## Overview

This project demonstrates how Android applications can monitor for specific target applications and present overlay interfaces. It leverages several Android system APIs to detect when particular apps are in the foreground and can display custom UI components over them.

## Technical Components

- **Background Monitoring Service**: Continuously monitors foreground applications
- **Boot-Persistent Operation**: Automatically starts monitoring after device restart
- **Permission Management**: Handles required permissions for overlay and usage monitoring
- **Target Application Detection**: Identifies specific applications and activities
- **Custom Interface Overlay**: Displays alternative interfaces when targets are detected

## Required Permissions

- `SYSTEM_ALERT_WINDOW`: For drawing content over other apps
- `PACKAGE_USAGE_STATS`: For monitoring application usage
- `RECEIVE_BOOT_COMPLETED`: For starting at device boot
- `FOREGROUND_SERVICE`: For persistent background operation
- `POST_NOTIFICATIONS`: For notification management

## Educational Purpose

This application is intended for:
- Security researchers studying overlay techniques
- App developers learning about Android security measures
- Educational demonstrations of permission-based vulnerabilities

## Disclaimer

This code is provided strictly for educational and research purposes. Using this application to intercept user data from other applications without explicit permission would violate privacy laws and terms of service agreements. Always respect user privacy and application security.

## License

[MIT License](LICENSE)
