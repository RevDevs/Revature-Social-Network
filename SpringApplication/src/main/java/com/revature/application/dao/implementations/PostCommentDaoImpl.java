package com.revature.application.dao.implementations;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.revature.application.dao.PostCommentDao;
import com.revature.application.dao.beans.Post;
import com.revature.application.dao.beans.PostComment;

@Service
public class PostCommentDaoImpl implements PostCommentDao{
	@Autowired
	SessionFactory sf;

	@Transactional
	public boolean create(PostComment comment) {
		Session session = sf.getCurrentSession();
		session.save(comment);
		session.flush();
		 
		return true;
	}

	@Transactional
	public List<Post> readAll() {		
		Session session = sf.getCurrentSession();
		
		List<Post> posts = session.createQuery("from Post post").list();
		session.flush();
		 
		return posts;
	}

	@Transactional
	public PostComment read(long comment_id) {
		Session session = sf.getCurrentSession();
		
		PostComment comment = session.get(PostComment.class, comment_id);
		session.flush();
		
		return comment;
	}

	@Transactional
	public boolean update(PostComment comment) {
		Session session = sf.getCurrentSession();
		
		session.saveOrUpdate(comment);
		session.flush();
		
		return true;
	}

	@Transactional
	public boolean delete(PostComment comment) {
		Session session = sf.getCurrentSession();
		
		session.delete(comment);
		session.flush();
		
		return true;
	}

	@Transactional
	public boolean deleteById(long comment_id) {
		Session session = sf.getCurrentSession();

		PostComment comment = new PostComment();
		comment.setCommentId(comment_id);
		
		session.delete(comment);
		session.flush();
		
		return true;
	}
	
}
