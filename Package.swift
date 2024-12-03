// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "AvaRemotePlugin",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "AvaRemotePlugin",
            targets: ["AvaRemotePluginPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "AvaRemotePluginPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/AvaRemotePluginPlugin"),
        .testTarget(
            name: "AvaRemotePluginPluginTests",
            dependencies: ["AvaRemotePluginPlugin"],
            path: "ios/Tests/AvaRemotePluginPluginTests")
    ]
)