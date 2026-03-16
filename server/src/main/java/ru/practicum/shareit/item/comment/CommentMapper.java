package ru.practicum.shareit.item.comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        CommentDto.CommentDtoBuilder builder = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated());

        if (comment.getAuthor() != null) {
            builder.authorName(comment.getAuthor().getName());
        }

        return builder.build();
    }
}
