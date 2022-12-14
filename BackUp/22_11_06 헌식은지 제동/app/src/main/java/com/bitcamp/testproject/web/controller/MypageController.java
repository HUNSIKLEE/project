package com.bitcamp.testproject.web.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import com.bitcamp.testproject.service.BoardService;
import com.bitcamp.testproject.service.MemberService;
import com.bitcamp.testproject.vo.AttachedFile;
import com.bitcamp.testproject.vo.Board;
import com.bitcamp.testproject.vo.Member;

@Controller
@RequestMapping("/mypage/")
public class MypageController {

  @Autowired
  ServletContext sc;
  @Autowired
  BoardService boardService;
  @Autowired
  MemberService memberService;


  @GetMapping("my-post")
  public String partyPost(Member member) {
    return "mypage/myPost";
  }

  @GetMapping("party-management")
  public String partyMgmt(Member member/* , Party party */) {
    return "mypage/partyMgmt";
  }

  @GetMapping("health-management")
  public String partyHMgmt(Member member) {
    return "mypage/healthMgmt";
  }















  @GetMapping("form")
  public void form() throws Exception {
  }

  @PostMapping("add") 
  public String add(
      Board board,
      MultipartFile[] files,
      HttpSession session) throws Exception {

    board.setAttachedFiles(saveAttachedFiles(files));
    board.setWriter((Member) session.getAttribute("loginMember"));

    boardService.add(board);
    return "redirect:list";
  }

  private List<AttachedFile> saveAttachedFiles(Part[] files)
      throws IOException, ServletException {
    List<AttachedFile> attachedFiles = new ArrayList<>();
    String dirPath = sc.getRealPath("/board/files");

    for (Part part : files) {
      if (part.getSize() == 0) {
        continue;
      }

      String filename = UUID.randomUUID().toString();
      part.write(dirPath + "/" + filename);
      attachedFiles.add(new AttachedFile(filename));
    }
    return attachedFiles;
  }

  private List<AttachedFile> saveAttachedFiles(MultipartFile[] files)
      throws IOException, ServletException {
    List<AttachedFile> attachedFiles = new ArrayList<>();
    String dirPath = sc.getRealPath("/board/files");

    for (MultipartFile part : files) {
      if (part.isEmpty()) {
        continue;
      }

      String filename = UUID.randomUUID().toString();
      part.transferTo(new File(dirPath + "/" + filename));
      attachedFiles.add(new AttachedFile(filename));
    }
    return attachedFiles;
  }

  //  @GetMapping("list")
  //  public void list(Model model, int no) throws Exception {
  //    model.addAttribute("boards", boardService.list(no));
  //  }

  @GetMapping("detail")
  public Map detail(int no) throws Exception {
    Board board = boardService.get(no);
    if (board == null) {
      throw new Exception("?????? ????????? ???????????? ????????????!");
    }

    Map map = new HashMap();
    map.put("board", board);
    return map;
  }

  @PostMapping("update")
  public String update(
      Board board,
      Part[] files,
      HttpSession session) 
          throws Exception {

    board.setAttachedFiles(saveAttachedFiles(files));

    checkOwner(board.getNo(), session);

    if (!boardService.update(board)) {
      throw new Exception("???????????? ????????? ??? ????????????!");
    }

    return "redirect:list";
  }

  private void checkOwner(int boardNo, HttpSession session) throws Exception {
    Member loginMember = (Member) session.getAttribute("loginMember");
    if (boardService.get(boardNo).getWriter().getNo() != loginMember.getNo()) {
      throw new Exception("????????? ???????????? ????????????.");
    }
  }

  @GetMapping("delete")
  public String delete(
      int no, 
      HttpSession session) 
          throws Exception {

    checkOwner(no, session);
    if (!boardService.delete(no)) {
      throw new Exception("???????????? ????????? ??? ????????????.");
    }

    return "redirect:list";
  }

  @GetMapping("fileDelete")
  public String fileDelete(
      int no,
      HttpSession session) 
          throws Exception {

    AttachedFile attachedFile = boardService.getAttachedFile(no); 

    Member loginMember = (Member) session.getAttribute("loginMember");
    Board board = boardService.get(attachedFile.getObjectNo()); 

    if (board.getWriter().getNo() != loginMember.getNo()) {
      throw new Exception("????????? ???????????? ????????????.");
    }

    if (!boardService.deleteAttachedFile(no)) {
      throw new Exception("????????? ??????????????? ????????? ??? ????????????.");
    }

    return "redirect:detail?no=" + board.getNo();
  }
}






