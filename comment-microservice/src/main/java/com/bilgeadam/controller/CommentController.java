package com.bilgeadam.controller;

import com.bilgeadam.repository.entity.Comment;
import com.bilgeadam.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.bilgeadam.constant.ApiUrls.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(COMMENT)
@CrossOrigin(origins = "*" ,allowedHeaders = "*")
public class CommentController {

    private final CommentService commentService;


    @GetMapping(GET_COMMENTS)
    public ResponseEntity<List<Comment>> getComments(){
        return ResponseEntity.ok(commentService.getComments());
    }


    @GetMapping(GET_PENDING_COMMENTS)
    public ResponseEntity<List<Comment>> getPendingComments(){
        return ResponseEntity.ok(commentService.getPendingComments());
    }

    @PutMapping(ACTIVATE_COMMENT)
    public ResponseEntity<Boolean> activeComment(String id){
        return ResponseEntity.ok(commentService.activeComment(id));
    }

    @PutMapping(DENIED_COMMENT)
    public ResponseEntity<Boolean> deniedComment(String id){
        return ResponseEntity.ok(commentService.deniedComment(id));
    }

}
