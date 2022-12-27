package com.example.demo.service;

import java.util.SplittableRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepostory;

	@Autowired
	private JavaMailSender javaMailSender;

	// User Registration
	public String saveUser(User user) {
		System.out.println("Inside Userservice Saveuser Method is executing");
		try {
			if (user != null) {
				BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
				String encPass = bc.encode(user.getPassWord());
				user.setPassWord(encPass);
				User userFrmDb = userRepostory.save(user);
				if (userFrmDb != null) {
					return "User Registered Successsfully";
				} else {
					return "Failed to register the user";
				}
			} else {
				return "received invalid data";
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			return "Operation Failed";
		}
	}

	public Object getDetailsById(int id) {
		return userRepostory.findAll();
	}

	// User LogIn
	public String userLogin(User user) {
		System.out.println("Inside LogIn method!!");
		try {
			if (user != null) {
				User ud = userRepostory.findByUserId(user.getUserId());
				if (ud != null) {
					// ud.getPassWord().equals(user.getPassWord())
					BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
					if (bc.matches(user.getPassWord(), ud.getPassWord())) {
						return "Login successful !!!!******$$$$$$@@@@@@^^^^^^^";
					} else {
						return "Invalid password";
					}
				} else {
					return "User Id not found";
				}
			} else {
				return "Invalid data";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Opreation filed";
		}
	}

	// User password update
	public String updatePassword(User user) {
		System.out.println("Inside Password update method");
		try {
			if (user != null) {
				User usr = userRepostory.findByUserId(user.getUserId());
				if (usr != null) {
					BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
					String encPass = bc.encode(user.getPassWord());// (user.getPassWord());
					user.setEmail(usr.getEmail());
					user.setPassWord(encPass);
					userRepostory.save(user);
					return "Password updated successfully";
				} else {
					return "User id not found";
				}
			} else {
				return "Invalid data";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "Unable to update password";
		}
	}

	// Mail sending
	public void EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	// Random number generator
	public static String otpGenerator(int otpLength) {
		SplittableRandom splittableRandom = new SplittableRandom();

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < otpLength; i++) {
			sb.append(splittableRandom.nextInt(0, 10));
		}
		return sb.toString();
	}

	public String forgetPwd(User user) {
		String otp = otpGenerator(6);
		System.out.println("I am inside forgetPwd method");
		User ur = userRepostory.getReferenceById(user.getUserId());
		try {
			if (user.getUserId().matches(ur.getUserId())) {
				if (user.getEmail().matches(ur.getEmail())) {
					System.out.println("Please wait trying to send mail");
					SimpleMailMessage mailMessage = new SimpleMailMessage();
					mailMessage.setTo(ur.getEmail());
					mailMessage.setSubject("Password Recovery mail");
					mailMessage.setText("Your new Password is: \n\n" + otp);
					mailMessage.setFrom("austin.smith6970@gmail.com");
					BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
					String encPass = bc.encode(otp);
					user.setEmail(ur.getEmail());
					user.setPassWord(encPass);
					userRepostory.save(user);
					javaMailSender.send(mailMessage);
					System.out.println("Email sent please check your mail!!!");
					return "Email sent successfully!!!!";
				} else {
					return "Email does not match!!";
				}
			} else {
				return "Email does not matched";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "opreation failed";
		}

	}

}
