// This test is compile-only for IOS
// DISABLE_NATIVE: isAppleTarget=false
// DISABLE_NATIVE: targetFamily=OSX
// DISABLE_NATIVE: targetFamily=TVOS
// DISABLE_NATIVE: targetFamily=WATCHOS

import platform.UIKit.UIViewController

class ViewController : UIViewController {
    @OverrideInit
    constructor() : super(nibName = null, bundle = null)
}

fun box() = "OK"
