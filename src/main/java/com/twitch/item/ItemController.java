package com.twitch.item;


import com.twitch.model.TypeGroupedItemList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDate;
import java.util.List;


@RestController
public class ItemController {


    private final ItemService itemService;


    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }


    @GetMapping("/search")
    public TypeGroupedItemList search(@RequestParam("game_id") String gameId) {
        return itemService.getItems(gameId);
    }
}