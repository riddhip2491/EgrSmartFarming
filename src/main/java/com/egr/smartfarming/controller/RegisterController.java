package com.egr.smartfarming.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import com.egr.smartfarming.model.*;
import com.egr.smartfarming.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

@Controller
public class RegisterController {
	
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	private UserService userService;
	private EmailService emailService;

    private Weather weather;
    @Value("${openweather.apikey}")
    private String weatherApiKey;

    @Value("${openweather.url}")
    private String weatherApi;


    private RestService restService;

    private RainfallService rainfallService;
    private SoilMoistAWSService soilMoistAWSService;

    private StartUpService startUpService;

    private DecisionService decisionService;

    HttpSession session;
	
	@Autowired
	public RegisterController(BCryptPasswordEncoder bCryptPasswordEncoder,
			UserService userService, EmailService emailService, RainfallService rainfallService,
                              SoilMoistAWSService soilMoistAWSService, StartUpService startUpService,
                              DecisionService decisionService){
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.emailService = emailService;
		this.restService = new RestService();
		this.weather = new Weather();
		this.rainfallService = rainfallService;
		this.soilMoistAWSService = soilMoistAWSService;
		this.startUpService = startUpService;
		this.decisionService = decisionService;
	}
	
	// Return registration form template
	@RequestMapping(value="/register", method = RequestMethod.GET)
	public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
		modelAndView.addObject("user", user);
		modelAndView.setViewName("register");
		return modelAndView;
	}
	
	// Process form input data
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ModelAndView processRegistrationForm(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult, HttpServletRequest request) {
				
		// Lookup user in database by e-mail
		User userExists = userService.findByEmail(user.getEmail());
        User usernameExists = userService.findByUsername(user.getUsername());
		System.out.println(userExists);
		
		if (userExists != null) {
			modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is already a user registered with the email provided.");
			modelAndView.setViewName("register");
			bindingResult.reject("email");
		}

        if(usernameExists != null){
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!   There is already a user registered with the uesrname provided.");
            modelAndView.setViewName("register");
            bindingResult.reject("username");
        }
			
		if (bindingResult.hasErrors()) { 
			modelAndView.setViewName("register");		
		} else { // new user so we create user and send confirmation e-mail
					
			// Disable user until they click on confirmation link in email
		    user.setEnabled(false);
		      
		    // Generate random 36-character string token for confirmation link
		    user.setConfirmationToken(UUID.randomUUID().toString());
		        
		    userService.saveUser(user);
				
			String appUrl = request.getScheme() +  "://" + request.getServerName() + ":8080";
			
			SimpleMailMessage registrationEmail = new SimpleMailMessage();
			registrationEmail.setTo(user.getEmail());
			registrationEmail.setSubject("Registration Confirmation");
			registrationEmail.setText("To confirm your e-mail address, please click the link below:\n"
					+ appUrl + "/confirm?token=" + user.getConfirmationToken());
			registrationEmail.setFrom("SmartFarming2491@gmail.com");
			
			emailService.sendEmail(registrationEmail);
			
			modelAndView.addObject("confirmationMessage", "A confirmation e-mail has been sent to " + user.getEmail());
			modelAndView.setViewName("register");
		}
			
		return modelAndView;
	}
	
	// Process confirmation link
	@RequestMapping(value="/confirm", method = RequestMethod.GET)
	public ModelAndView confirmRegistration(ModelAndView modelAndView, @RequestParam("token") String token) {
			
		User user = userService.findByConfirmationToken(token);
			
		if (user == null) { // No token found in DB
			modelAndView.addObject("invalidToken", "Oops!  This is an invalid confirmation link.");
		} else { // Token found
			modelAndView.addObject("confirmationToken", user.getConfirmationToken());
		}
			
		modelAndView.setViewName("confirm");
		return modelAndView;		
	}
	
	// Process confirmation link
	@RequestMapping(value="/confirm", method = RequestMethod.POST)
	public ModelAndView confirmRegistration(ModelAndView modelAndView, BindingResult bindingResult, @RequestParam Map<String, String> requestParams, RedirectAttributes redir) {
				
		modelAndView.setViewName("confirm");
		
		Zxcvbn passwordCheck = new Zxcvbn();
		
		Strength strength = passwordCheck.measure(requestParams.get("password"));
		
		if (strength.getScore() < 3) {
			//modelAndView.addObject("errorMessage", "Your password is too weak.  Choose a stronger one.");
			bindingResult.reject("password");
			
			redir.addFlashAttribute("errorMessage", "Your password is too weak.  Choose a stronger one.");

			modelAndView.setViewName("redirect:confirm?token=" + requestParams.get("token"));
			System.out.println(requestParams.get("token"));
			return modelAndView;
		}
	
		// Find the user associated with the reset token
		User user = userService.findByConfirmationToken(requestParams.get("token"));

		// Set new password
		user.setPassword(bCryptPasswordEncoder.encode(requestParams.get("password")));

		// Set user to enabled
		user.setEnabled(true);
		
		// Save user
		userService.saveUser(user);
		
		//modelAndView.addObject("successMessage", "Your password has been set!");

        modelAndView.setViewName("redirect:login" );
		return modelAndView;		
	}

    @RequestMapping(value="/login1", method = RequestMethod.POST)
    public ModelAndView processLoginForm(ModelAndView modelAndView, @Valid UserGeoLocation user, BindingResult bindingResult, HttpServletRequest request,RedirectAttributes redir){
        // Lookup user in database by e-mail
        System.out.println("Request param: " + user.getEmail());
        User userExists = userService.findByEmail(user.getEmail());
        request.getSession().setAttribute("user", user);
       // System.out.println(userExists);
        weather.setLatitude(user.getLatitude());
        weather.setLongitude(user.getLongitude());
        //session.setAttribute("user", user);
        if (userExists == null) {
            modelAndView.addObject("alreadyRegisteredMessage", "Oops!  There is no user registered with the email provided.");

			User userObj = new User();
			userObj.setEmail(user.getEmail());
			userObj.setPassword(user.getPassword());
			modelAndView.addObject("user",userObj);
            modelAndView.setViewName("login");
            bindingResult.reject("email");
        }else{


            if(bCryptPasswordEncoder.matches(user.getPassword(),userExists.getPassword())){

                modelAndView.setViewName("redirect:dashboard");

              modelAndView.getModelMap().addAttribute("email",user.getEmail());

            }else{
				User userObj = new User();
				userObj.setEmail(user.getEmail());
				userObj.setPassword(user.getPassword());
				modelAndView.addObject("user",userObj);
                modelAndView.addObject("alreadyRegisteredMessage", "Email or Password do not match");
                modelAndView.setViewName("login");
            }
        }


        return modelAndView;
    }

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public ModelAndView showLoginPage(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

    @RequestMapping(value="/dashboard", method = RequestMethod.GET)
    public ModelAndView showHomePage(ModelAndView modelAndView, UserGeoLocation user, HttpServletRequest request){
		if(request.getSession().getAttribute("user")== null){
		    user = null;
            modelAndView.setViewName("redirect:login");
            return modelAndView;
        }

		//Getting curent weather condition
        final String uri = weatherApi + "?lat=" + weather.getLatitude() + "&units=metric" + "&lon=" + weather.getLongitude() + "&APPID=" + weatherApiKey;
        restService.getLocationObject(uri,weather);
        //End

		//Get Latest Rain Data From AWS
		Rainfall rainfall = rainfallService.getLatestRainData();
		System.out.println("Latest Battery Voltage: " + rainfall.getBatteryVoltage());
		//End

        //Get Latest Soil Moisture Data from AWs
       SoilMoisture soilMoisture = soilMoistAWSService.getLatestSoilMoistData();
        System.out.println("Latest Soil Moisture DAtA: " + soilMoisture.getBatteryVoltage());
        //modelAndView.addObject("user", user);
        modelAndView.addObject("temperature", weather.getCurrentTemperature());
        modelAndView.addObject("description", weather.getDescription());
		modelAndView.addObject("rainaws",soilMoisture.getBatteryVoltage());
        modelAndView.setViewName("dashboard");
        return modelAndView;
    }

	@RequestMapping(value="/maps", method = RequestMethod.GET)
	public ModelAndView showMap(ModelAndView modelAndView,  HttpServletRequest request){
		if(request.getSession().getAttribute("user")== null){
			modelAndView.setViewName("redirect:login");
			System.out.println("SEssion null");
			return modelAndView;
		}
		//modelAndView.addObject("user", user);
		modelAndView.setViewName("maps");
		return modelAndView;
	}

	@RequestMapping(value="/user", method = RequestMethod.GET)
	public ModelAndView showUserProfile(ModelAndView modelAndView, User user, HttpServletRequest request){
		if(request.getSession().getAttribute("user")== null){
			modelAndView.setViewName("redirect:login");
			System.out.println("SEssion null");
			return modelAndView;
		}
		modelAndView.addObject("user", user);
		modelAndView.setViewName("user");
		return modelAndView;
	}

    @RequestMapping(value="/decision", method = RequestMethod.GET)
    public ModelAndView showDecision(ModelAndView modelAndView,HttpServletRequest request){
        if(request.getSession().getAttribute("user")== null){
            modelAndView.setViewName("redirect:login");
            System.out.println("SEssion null");
            return modelAndView;
        }
	    //add soil type
        ArrayList<String> soils =startUpService.getSoilTypes();
        ArrayList<String> seasons = startUpService.findAllSeasons();
        //Iterator iterator = soils.iterator();
        modelAndView.addObject("soilTypes", soils);
        modelAndView.addObject("seasons", seasons);
        //modelAndView.addObject("user", user);
        modelAndView.setViewName("decision");
        return modelAndView;
    }

    @RequestMapping(value = "/decision", method = RequestMethod.POST)
    public ModelAndView processDecision(ModelAndView modelAndView, @Valid Decision decision, @RequestParam String latitude,HttpServletRequest request, @RequestParam String longitude, BindingResult bindingResult,RedirectAttributes redir){
		if(request.getSession().getAttribute("user")== null){
			modelAndView.setViewName("redirect:login");
			System.out.println("SEssion null");
			return modelAndView;
		}
    	System.out.println("Entering into Decison Post");
        String getSoil= decision.getSoilName();
        String lat = latitude;
        String lon = longitude;
        //Get Decision
      //  ArrayList<Decision> getDecision = decisionService.processDecision(decision, latitude, longitude);
        ArrayList<Decision> getDecision = decisionService.processFinalDecision(decision,latitude,longitude);

		if (getDecision.size()> 0) {
		    ArrayList<String> cropList = new ArrayList<>();
		    for(int i =0; i< getDecision.size();i++){
		        cropList.add(getDecision.get(i).getCropName());
            }
            Set<String> set = new LinkedHashSet<>(cropList);
			modelAndView.addObject("result", "Result of your Selection");
            modelAndView.addObject("confirmCropName", set);

			modelAndView.addObject("confirmStartMonth", getDecision.get(0).getStartMonth());
			modelAndView.addObject("confirmEndMonth", getDecision.get(0).getEndMonth());
		} else{
			modelAndView.addObject("alreadyRegisteredMessage", "No crop suggestion. Please select another values");
		}
        //add soil type
        ArrayList<String> soils =startUpService.getSoilTypes();
        modelAndView.addObject("soilTypes", soils);
		ArrayList<String> seasons = startUpService.findAllSeasons();
		modelAndView.addObject("seasons", seasons);
        modelAndView.setViewName("decision");
        return modelAndView;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
	public ModelAndView logout(ModelAndView modelAndView,HttpServletRequest request){

		if(request.getSession().getAttribute("user")== null){
			request.removeAttribute("user");
			modelAndView.setViewName("redirect:login");
			System.out.println("Session null");
		}else {
			request.getSession().removeAttribute("user");

			modelAndView.setViewName("redirect:login");
		}
		return modelAndView;
	}
}