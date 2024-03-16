package edu.java.bot.api.updates;

import edu.java.bot.api.exception.UpdateAlreadyExistsException;
import edu.java.bot.api.updates.dto.LinkUpdateRequest;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class UpdatesService {
    private final Set<LinkUpdateRequest> updateRequests = new HashSet<>(); // TODO remove when data layer is done

    public void createUpdate(LinkUpdateRequest linkUpdateRequest) {
        if (!updateRequests.add(linkUpdateRequest)) {
            throw new UpdateAlreadyExistsException("Update с переданным id уже обработан");
        }
    }
}
