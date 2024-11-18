# KtorTest

> java.lang.IllegalArgumentException: Array has more than one element.

the reproduction of [KTOR-7298](https://youtrack.jetbrains.com/issue/KTOR-7298)

[get apk file in github workflows](https://github.com/lisonge/KtorTest/actions/workflows/Build-Apk.yml)

| ok                                                                                        | error                                                                                     |
| ----------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| ![image](https://github.com/user-attachments/assets/5bbf37b5-54bd-43b2-9147-cf3c99336d58) | ![image](https://github.com/user-attachments/assets/a506a476-646e-4c9d-bfa4-7d89dc8344a2) |

This is a very strange bug, and I suspect it’s an issue with one of the build tools.

In `app/build.gradle.kts`, the dependency is declared as implementation but is not used in the project; however, it still causes a bug

Adding `-keep class com.hjq.toast.** {*;}` or `-keep class coil.**{*;}` or some others to `app/proguard-rules.pro` also avoids the bug
