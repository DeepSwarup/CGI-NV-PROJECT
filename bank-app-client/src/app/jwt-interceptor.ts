import { HttpInterceptorFn } from '@angular/common/http';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  
  const token = localStorage.getItem('token')

    if (req.url.includes('/login') || req.url.includes('/signup')) {
    return next(req);
  }

  if(token){
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    })
    return next(cloned)
  }

  return next(req)

};
