package thuytrinh.forecastbird.forecast;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import thuytrinh.forecastbird.R;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {
  @Rule public ActivityTestRule<MainActivity> activityTestRule
      = new ActivityTestRule<>(MainActivity.class);

  private static Matcher<View> childAtPosition(
      final Matcher<View> parentMatcher,
      final int position) {
    return new TypeSafeMatcher<View>() {
      @Override public void describeTo(Description description) {
        description.appendText("Child at position " + position + " in parent ");
        parentMatcher.describeTo(description);
      }

      @Override public boolean matchesSafely(View view) {
        ViewParent parent = view.getParent();
        return parent instanceof ViewGroup && parentMatcher.matches(parent)
            && view.equals(((ViewGroup) parent).getChildAt(position));
      }
    };
  }

  @Test public void showInitialForecast() {
    // The initial forecast isn't empty, thus the empty view should be gone.
    onView(withId(R.id.emptyView)).check(matches(not(isDisplayed())));
    assertItemView(0, "Sunny", "Friday, August 19, 2016", "24", "17");
    assertItemView(1, "Showers", "Saturday, August 20, 2016", "25", "16");
    assertItemView(2, "Scattered Showers", "Sunday, August 21, 2016", "27", "19");
  }

  @Test public void swipeDownAndShowUpdatedForecast() {
    ViewInteraction forecastItemsView = onView(
        allOf(withId(R.id.forecastItemsView), isDisplayed()));

    // Swipe down for the first time.
    forecastItemsView.perform(swipeDown());

    // We got forecast updated and because it's not empty,
    // the empty view should be gone.
    onView(withId(R.id.emptyView)).check(matches(not(isDisplayed())));
    assertItemView(0, "Sunny", "Monday, August 22, 2016", "21", "11");
    assertItemView(1, "Showers", "Tuesday, August 23, 2016", "22", "12");
    assertItemView(2, "Scattered Showers", "Wednesday, August 24, 2016", "23", "13");

    // Swipe down again. All forecast should be gone and
    // the empty view should be visible.
    forecastItemsView.perform(swipeDown());
    onView(withId(R.id.emptyView)).check(matches(isDisplayed()));
  }

  private void assertItemView(
      int itemPosition,
      String text,
      String date,
      String highestTemperature,
      String lowestTemperature) {
    ViewInteraction textView = onView(
        allOf(withId(R.id.textView), withText(text),
              childAtPosition(
                  childAtPosition(
                      withId(R.id.forecastItemsView),
                      itemPosition),
                  0),
              isDisplayed()));
    textView.check(matches(withText(text)));

    ViewInteraction dateView = onView(
        allOf(withId(R.id.dateView), withText(date),
              childAtPosition(
                  childAtPosition(
                      withId(R.id.forecastItemsView),
                      itemPosition),
                  1),
              isDisplayed()));
    dateView.check(matches(withText(date)));

    ViewInteraction highestTemperatureView = onView(
        allOf(withId(R.id.highestTemperatureView), withText(highestTemperature),
              childAtPosition(
                  childAtPosition(
                      withId(R.id.forecastItemsView),
                      itemPosition),
                  2),
              isDisplayed()));
    highestTemperatureView.check(matches(withText(highestTemperature)));

    ViewInteraction lowestTemperatureView = onView(
        allOf(withId(R.id.lowestTemperatureView), withText(lowestTemperature),
              childAtPosition(
                  childAtPosition(
                      withId(R.id.forecastItemsView),
                      itemPosition),
                  3),
              isDisplayed()));
    lowestTemperatureView.check(matches(withText(lowestTemperature)));
  }
}
