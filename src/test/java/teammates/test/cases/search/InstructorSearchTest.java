package teammates.test.cases.search;

import java.util.Arrays;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.storage.api.InstructorsDb;
import teammates.storage.search.InstructorSearchDocument;
import teammates.storage.search.InstructorSearchQuery;
import teammates.test.driver.AssertHelper;

/**
 * SUT: {@link InstructorsDb}, {@link InstructorSearchDocument},
 * {@link InstructorSearchQuery}.
 */
public class InstructorSearchTest extends BaseSearchTest {
    @Test
    public void allTests() {
        InstructorsDb instructorsDb = new InstructorsDb();

        InstructorAttributes ins1InCourse1 = dataBundle.instructors.get("instructor1OfCourse1");
        InstructorAttributes ins2InCourse1 = dataBundle.instructors.get("instructor2OfCourse1");
        InstructorAttributes ins1InCourse2 = dataBundle.instructors.get("instructor1OfCourse2");
        InstructorAttributes ins2InCourse2 = dataBundle.instructors.get("instructor2OfCourse2");
        InstructorAttributes insInArchivedCourse = dataBundle.instructors.get("instructorOfArchivedCourse");
        InstructorAttributes insInUnregCourse = dataBundle.instructors.get("instructor5");

        ______TS("success: search for instructors in whole system; query string does not match anyone");

        InstructorSearchResultBundle results =
                instructorsDb.searchInstructorsInWholeSystem("non-existent");
        verifySearchResults(results);

        ______TS("success: search for instructors in whole system; query string matches some instructors");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse1, ins1InCourse2);

        ______TS("success: search for instructors in whole system; query string should be case-insensitive");

        results = instructorsDb.searchInstructorsInWholeSystem("InStRuCtOr2");
        verifySearchResults(results, ins2InCourse1, ins2InCourse2);

        ______TS("success: search for instructors in whole system; instructors in archived courses should be included");

        results = instructorsDb.searchInstructorsInWholeSystem("archived");
        verifySearchResults(results, insInArchivedCourse);

        ______TS("success: search for instructors in whole system; instructors in unregistered course should be included");

        results = instructorsDb.searchInstructorsInWholeSystem("instructor5");
        verifySearchResults(results, insInUnregCourse);

        ______TS("success: search for instructors in whole system; deleted instructors no longer searchable");

        instructorsDb.deleteInstructor(ins1InCourse1.courseId, ins1InCourse1.email);
        results = instructorsDb.searchInstructorsInWholeSystem("instructor1");
        verifySearchResults(results, ins1InCourse2);
    }

    private static void verifySearchResults(InstructorSearchResultBundle actual,
            InstructorAttributes... expected) {
        assertEquals(expected.length, actual.numberOfResults);
        assertEquals(expected.length, actual.instructorList.size());
        AssertHelper.assertSameContentIgnoreOrder(Arrays.asList(expected), actual.instructorList);
    }
}
