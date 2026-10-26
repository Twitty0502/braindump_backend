package org.braindump;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.braindump.ai.IdeaCategoriesAiService;
import org.braindump.enums.IdeaCategories;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class IdeaCategoriesAiServiceTest {

    @Inject
    IdeaCategoriesAiService aiService;

    @Test
    void shouldCategorizeImprovementIdea() {
        String idea = "Vi borde förbättra inloggningsflödet så att det blir enklare.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.IMPROVEMENT, category);
    }

    @Test
    void shouldCategorizeNewIdea() {
        String idea = "Vi borde utveckla en ny app för att samla alla idéförslag.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.NEW_IDEA, category);
    }

    @Test
    void shouldCategorizeProblemSolvingIdea() {
        String idea = "Vi behöver hitta orsaken till att systemet kraschar och lösa problemet.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.PROBLEM_SOLVING, category);
    }

    @Test
    void shouldCategorizeProcessAndWaysOfWorkingIdea() {
        String idea = "Vi borde förbättra våra arbetsrutiner och skapa en tydligare process.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.PROCESS_AND_WAYS_OF_WORKING, category);
    }

    @Test
    void shouldCategorizeActivityAndProjectIdea() {
        String idea = "Vi borde anordna en gemensam utflykt där vi åker ut och fiskar.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.ACTIVITY_AND_PROJECT, category);
    }

    @Test
    void shouldCategorizeContentAndCommunicationIdea() {
        String idea = "Vi vill skapa ett nyhetsbrev för att informera alla medarbetare om företagets nyheter.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.CONTENT_AND_COMMUNICATION, category);
    }

    @Test
    void shouldCategorizeOtherIdea() {
        String idea = "Jag har ett förslag som inte passar in i någon av de andra kategorierna.";

        IdeaCategories category = aiService.categorizeIdea(idea);

        assertEquals(IdeaCategories.OTHER, category);
    }
}
