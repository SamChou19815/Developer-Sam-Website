from google.appengine.api import urlfetch
import webapp2
import urllib2

class HourlyCronPage(webapp2.RequestHandler):
    def get(self):
        rpc = urlfetch.create_rpc()
        urlfetch.make_fetch_call(
            rpc, 'https://developersam.com/apis/public/rss_reader/cron', validate_certificate=True)

app = webapp2.WSGIApplication([('/hourly', HourlyCronPage),], debug=True)
