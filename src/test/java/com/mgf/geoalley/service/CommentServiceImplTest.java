package com.mgf.geoalley.service;

import com.mgf.geoalley.exceptions.CommentNotFoundException;
import com.mgf.geoalley.model.Comment;
import com.mgf.geoalley.model.Map;
import com.mgf.geoalley.model.User;
import com.mgf.geoalley.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private CommentRepository mockCommentRepository;

    private CommentServiceImpl commentServiceImplUnderTest;

    @BeforeEach
    void setUp() {
        commentServiceImplUnderTest = new CommentServiceImpl(mockCommentRepository);
    }

  @Test
  public void testAddComment() {

        final Comment comment = new Comment(new User("username", "email", "password"),
                new Map("url", "description", new User("username", "email", "password")), "content");

        final Comment comment1 = new Comment(new User("username", "email", "password"),
                new Map("url", "description", new User("username", "email", "password")), "content");
        when(mockCommentRepository.save(any(Comment.class))).thenReturn(comment1);

        commentServiceImplUnderTest.addComment(comment);

        verify(mockCommentRepository).save(any(Comment.class));
    }

    @Test
    public void testRemoveById() throws Exception {
    	
        final Optional<Comment> comment = Optional.of(new Comment(new User("username", "email", "password"),
                new Map("url", "description", new User("username", "email", "password")), "content"));
        when(mockCommentRepository.findById(0)).thenReturn(comment);

        commentServiceImplUnderTest.removeById(0);

       verify(mockCommentRepository).delete(any(Comment.class));
    }

    @Test
    public void testRemoveById_CommentRepositoryFindByIdReturnsAbsent() {
        when(mockCommentRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentServiceImplUnderTest.removeById(0))
                .isInstanceOf(CommentNotFoundException.class);
    }

    @Test
    public void testRemoveById_CommentRepositoryDeleteThrowsException() {

    	final Optional<Comment> comment = Optional.of(new Comment(new User("username", "email", "password"),
                new Map("url", "description", new User("username", "email", "password")), "content"));
        when(mockCommentRepository.findById(0)).thenReturn(comment);

        doThrow(OptimisticLockingFailureException.class).when(mockCommentRepository).delete(any(Comment.class));

        assertThatThrownBy(() -> commentServiceImplUnderTest.removeById(0))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    public void testFindById() throws Exception {

        final Optional<Comment> comment = Optional.of(new Comment(new User("username", "email", "password"),
                new Map("url", "description", new User("username", "email", "password")), "content"));
        when(mockCommentRepository.findById(0)).thenReturn(comment);

        final Comment result = commentServiceImplUnderTest.findById(0);

    }

    @Test
    public void testFindById_CommentRepositoryReturnsAbsent() {
       
        when(mockCommentRepository.findById(0)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentServiceImplUnderTest.findById(0)).isInstanceOf(CommentNotFoundException.class);
    }

}
