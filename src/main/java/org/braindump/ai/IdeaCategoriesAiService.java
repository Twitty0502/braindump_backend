package org.braindump.ai;

import org.braindump.enums.IdeaCategories;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface IdeaCategoriesAiService {

    @SystemMessage("""
            Du kategoriserar idéer för applikationen BrainDump.
            Välj exakt den kategori som bäst beskriver idén.
            Returnera endast ett giltigt värde från enumen IdeaCategories.
            Tillåtna värden är följande:
             NEW_IDEA,
            IMPROVEMENT,
            PROBLEM_SOLVING,
            PROCESS_AND_WAYS_OF_WORKING,
            ACTIVITY_AND_PROJECT,
            CONTENT_AND_COMMUNICATION,
            OTHER.

            Om idén är otydlig eller inte passar någon annan kategori, välj då OTHER.
            """)
    @UserMessage("Kategorisera följande idé:\n{{ideaText}}")
    IdeaCategories categorizeIdea(String ideaText);
}
