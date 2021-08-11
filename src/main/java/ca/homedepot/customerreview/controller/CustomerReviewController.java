package ca.homedepot.customerreview.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.homedepot.customerreview.dao.ProductDao;
import ca.homedepot.customerreview.dao.UserDao;
import ca.homedepot.customerreview.exception.ProductNotFoundException;
import ca.homedepot.customerreview.exception.UserNotFoundException;
import ca.homedepot.customerreview.forms.CustomerReviewForm;
import ca.homedepot.customerreview.model.CustomerReviewModel;
import ca.homedepot.customerreview.model.ProductModel;
import ca.homedepot.customerreview.model.UserModel;
import ca.homedepot.customerreview.service.CustomerReviewService;
import ca.homedepot.customerreview.util.ServicesUtil;


@RestController
public class CustomerReviewController
{
	@Autowired
	private ProductDao productDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private CustomerReviewService customerReviewService;

	@GetMapping({ "products/{productId:\\d+}/reviews" })
	public List<CustomerReviewModel> getReviews(@PathVariable final Long productId,
			@RequestParam(required = false) final Double ratingFrom, @RequestParam(required = false) final Double ratingTo)
	{
		final ProductModel product = productDao.findOne(productId);
		if (product == null)
		{
			throw new ProductNotFoundException(productId);
		}
		if(ratingFrom!=null && ratingTo!=null) {
			if(ratingFrom>ratingTo) {
				return customerReviewService.getReviewsForProduct(product, ratingTo, ratingFrom);
			}else {
				return customerReviewService.getReviewsForProduct(product, ratingFrom, ratingTo);
			}
		}
		return customerReviewService.getReviewsForProduct(product);
	}

//	@PostMapping({ "products/{productId:\\d+}/users/{userId:\\d+}/reviews" })
//	public CustomerReviewModel createReview(@PathVariable final Long userId, @PathVariable final Long productId,
//			@RequestBody final CustomerReviewForm customerReviewForm)
//	{
//		final ProductModel product = productDao.findOne(productId);
//		if (product == null)
//		{
//			throw new ProductNotFoundException(productId);
//		}
//
//		final UserModel user = userDao.findOne(userId);
//		if (user == null)
//		{
//			throw new UserNotFoundException(userId);
//		}
//
//		return customerReviewService
//				.createCustomerReview(customerReviewForm.getRating(), customerReviewForm.getHeadline(),
//						customerReviewForm.getComment(), product, user);
//	}
	
	@PostMapping({ "products/{productId:\\d+}/users/{userId:\\d+}/reviews" })
	public ResponseEntity<CustomerReviewModel> createReview(@PathVariable final Long userId, @PathVariable final Long productId,
			@RequestBody final CustomerReviewForm customerReviewForm)
	{
		final ProductModel product = productDao.findOne(productId);
		if (product == null)
		{
			throw new ProductNotFoundException(productId);
		}

		final UserModel user = userDao.findOne(userId);
		if (user == null)
		{
			throw new UserNotFoundException(userId);
		}
		if(customerReviewForm.getRating()<0 || ServicesUtil.hasCurseWords(customerReviewForm.getComment()) || ServicesUtil.hasCurseWords(customerReviewForm.getHeadline())) {
			return new ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY);
			
		}else {
			return  ResponseEntity.ok(customerReviewService
					.createCustomerReview(customerReviewForm.getRating(), customerReviewForm.getHeadline(),
							customerReviewForm.getComment(), product, user));
		}
		
	}

	@PostMapping({ "products" })
	public ProductModel createProduct()
	{
		final ProductModel product = new ProductModel();
		productDao.save(product);
		return product;
	}

	@PostMapping({ "users" })
	public UserModel createUser()
	{
		final UserModel user = new UserModel();
		userDao.save(user);
		return user;
	}

	@DeleteMapping({ "reviews/{reviewId:\\d+}" })
	public void deleteReview(@PathVariable final Long reviewId)
	{
		customerReviewService.deleteCustomerReview(reviewId);
	}
}
