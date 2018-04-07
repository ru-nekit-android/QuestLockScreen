package ru.nekit.android.qls.setupWizard

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import ru.nekit.android.shared.R
import ru.nekit.android.utils.IAutoDispose
import ru.nekit.android.utils.throttleClicks

abstract class BaseSetupWizardActivity : FragmentActivity(),
        IAutoDispose,
        ISetupWizardHolder {

    override var disposableMap: MutableMap<String, CompositeDisposable> = HashMap()

    protected val context: Context
        get() = applicationContext

    private val currentFragment: BaseSetupWizardFragment
        get() = supportFragmentManager.findFragmentByTag(FRAGMENT_NAME) as BaseSetupWizardFragment

    private lateinit var toolContainer: ViewGroup
    private lateinit var nextButton: Button
    private lateinit var altButton: Button
    private lateinit var fragmentContainer: ViewGroup
    override var unconditionedNextAction: Boolean = false

    override fun getToolContainer(): ViewGroup = toolContainer
    override fun getNextButton(): Button = nextButton
    override fun getAltButton(): Button = altButton

    override fun getView(): View = fragmentContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_wizard)
        toolContainer = findViewById(R.id.container_tool)
        fragmentContainer = findViewById(R.id.container_fragment)
        nextButton = findViewById(R.id.btn_next)
        altButton = findViewById(R.id.btn_alt)
        autoDisposeList {
            listOf(
                    nextButton.throttleClicks {
                        autoDispose {
                            nextAction().subscribe { it ->
                                if (it) showNextSetupWizardStep()
                            }
                        }
                    },
                    altButton.throttleClicks {
                        altAction()
                    }
            )
        }
        showNextSetupWizardStep()
    }

    override fun showNextSetupWizardStep() {
        autoDispose {
            setupWizard.nextStep.subscribe { it ->
                showSetupWizardStep(it)
            }
        }
    }

    override fun nextAction(): Single<Boolean> {
        return if (unconditionedNextAction)
            currentFragment.nextAction()
        else Single.zip(setupWizard.needLogin(setupWizard.currentStep!!), currentFragment.nextAction(),
                BiFunction { a: Boolean, b: Boolean -> a || b })
    }

    override fun altAction() = currentFragment.altAction()

    override fun onDestroy() {
        dispose()
        super.onDestroy()
    }

    abstract override fun showSetupWizardStep(step: ISetupWizardStep, vararg params: Any)

    protected fun replaceFragment(fragment: BaseSetupWizardFragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        //fragmentTransaction.setCustomAnimations(R.anim.slide_horizontal_in_short, R.anim.slide_horizontal_out_short);
        //fragmentTransaction.disallowAddToBackStack();
        //fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (fragment.addToBackStack) {
            fragmentTransaction.addToBackStack(null)
        }
        fragmentTransaction.replace(R.id.container_fragment, fragment, FRAGMENT_NAME)
        fragmentTransaction.commit()
        //Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_short);
        //Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_short);
        //fadeInAnimation.setStartTime(700);
        //AnimationSet mAnimationSet = new AnimationSet(true);
        //mAnimationSet.addAnimation(fadeOutAnimation);
        //mAnimationSet.addAnimation(fadeInAnimation);
        //mToolContainer.startAnimation(mAnimationSet);
        //RelativeLayout.LayoutParams fragmentContainerLayoutParams =
        //        (RelativeLayout.LayoutParams) mFragmentContainer.getLayoutParams();
        //int margins[] = mCurrentFragment.getContainerMargins(getResources());
        //fragmentContainerLayoutParams.setMargins(margins[0], margins[1], margins[2], margins[3]);
        //mFragmentContainer.requestLayout();
    }

    companion object {
        private const val FRAGMENT_NAME = "SetupWizardFragment"
    }
}