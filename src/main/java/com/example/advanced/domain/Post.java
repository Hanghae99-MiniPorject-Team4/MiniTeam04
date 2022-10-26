package com.example.advanced.domain;

import com.example.advanced.controller.request.PostRequestDto;
import java.util.List;
import javax.persistence.*;

import lombok.*;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column
  private String content;

  @Column
  private String images;

  @JoinColumn(name = "user_id")
  @ManyToOne(fetch = FetchType.EAGER)
  private Member member;

  @OneToMany(mappedBy = "post", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
  private List<Comment> commentList;

  @OneToOne
  private Files files;

  @Convert(converter = CategoryConverter.class)
  private Category category;



  public void update(PostRequestDto postRequestDto) {
    this.title = postRequestDto.getTitle();
    this.content = postRequestDto.getContent();
  }

  public boolean validateMember(Member member) {
    return !this.member.equals(member);
  }

}
