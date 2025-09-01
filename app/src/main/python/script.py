import instaloader
import os
import time
from typing import List

class ProgressTracker:
    def __init__(self):
        self.current_post = 0
        self.total_posts = 0
        self.current_status = "Initializing..."
    
    def update_progress(self, current: int, total: int, status: str):
        self.current_post = current
        self.total_posts = total
        self.current_status = status

# Global progress tracker
progress_tracker = ProgressTracker()

def get_progress():
    """Get current progress information"""
    return {
        'current': progress_tracker.current_post,
        'total': progress_tracker.total_posts,
        'status': progress_tracker.current_status
    }

def download(profile):
    """Download all posts from a profile with progress tracking"""
    try:
        profile = profile.strip()
        progress_tracker.update_progress(0, 0, "Initializing download...")
        
        user = instaloader.Instaloader()
        user.save_metadata = False
        user.post_metadata_txt_pattern = ""
        user.dirname_pattern = f"/sdcard/InstaLoaderApp/{profile}"
        
        # Get profile and posts
        progress_tracker.update_progress(0, 0, "Connecting to Instagram...")
        insta_profile = instaloader.Profile.from_username(user.context, profile)
        posts = list(insta_profile.get_posts())
        
        total_posts = len(posts)
        progress_tracker.update_progress(0, total_posts, f"Found {total_posts} posts, starting download...")
        
        if total_posts == 0:
            progress_tracker.update_progress(0, 1, "No posts found or account is private")
            return
        
        # Download posts with progress tracking
        for i, post in enumerate(posts, 1):
            try:
                progress_tracker.update_progress(i, total_posts, f"Downloading post {i}/{total_posts}...")
                user.download_post(post, target="")
                time.sleep(0.1)  # Small delay to prevent rate limiting
            except Exception as e:
                progress_tracker.update_progress(i, total_posts, f"Error downloading post {i}: {str(e)}")
                continue
        
        progress_tracker.update_progress(total_posts, total_posts, "Download completed successfully!")
        
    except Exception as e:
        progress_tracker.update_progress(0, 1, f"Error: {str(e)}")
        raise e

def post_count(username):
    """Get the number of posts for a username"""
    try:
        username = username.replace(" ", "")
        L = instaloader.Instaloader()
        profile = instaloader.Profile.from_username(L.context, username)
        posts = profile.get_posts()
        return posts.count
    except Exception as e:
        return 0

def download_post_from_link(shortcode):
    """Download a single post from its shortcode with progress tracking"""
    try:
        progress_tracker.update_progress(0, 1, "Initializing download...")
        
        L = instaloader.Instaloader()
        L.save_metadata = False
        L.download_video_thumbnails = False
        L.post_metadata_txt_pattern = ""
        L.dirname_pattern = f"/sdcard/InstaLoaderApp/posts"
        
        # Ensure download directory exists
        os.makedirs("/sdcard/InstaLoaderApp/posts", exist_ok=True)
        
        progress_tracker.update_progress(0, 1, "Connecting to Instagram...")
        post = instaloader.Post.from_shortcode(L.context, shortcode)
        
        progress_tracker.update_progress(0, 1, "Downloading post content...")
        L.download_post(post, target="")
        
        progress_tracker.update_progress(1, 1, "Download completed successfully!")
        
    except Exception as e:
        progress_tracker.update_progress(0, 1, f"Error: {str(e)}")
        raise e

def check_connection():
    """Check if we can connect to Instagram"""
    try:
        L = instaloader.Instaloader()
        # Try to get a public profile to test connection
        profile = instaloader.Profile.from_username(L.context, "instagram")
        return True
    except:
        return False

def get_download_path():
    """Get the download path"""
    return "/sdcard/InstaLoaderApp/"

def test_download_functionality():
    """Test function to verify everything works"""
    try:
        # Test connection
        if not check_connection():
            return False, "Instagram connection failed"
        
        # Test download path
        download_path = get_download_path()
        os.makedirs(download_path, exist_ok=True)
        
        # Test basic functionality
        L = instaloader.Instaloader()
        L.save_metadata = False
        
        return True, "All tests passed"
    except Exception as e:
        return False, f"Test failed: {str(e)}"