from google.appengine.api import urlfetch
import webapp2
import urllib2

class HourlyCronPage(webapp2.RequestHandler):
    def get(self):
        urlfetch.fetch(
            'https://developersam.com/apis/public/rss_reader/cron', validate_certificate=True)

app = webapp2.WSGIApplication([
    ('/hourly', HourlyCronPage),
], debug=True)
